# RamaFest deployment

## Prerequisites

- Docker Engine with Compose v2
- A production PostgreSQL-compatible database, or the PostgreSQL service in `docker-compose.yml`
- DNS/TLS handled by a reverse proxy when exposing the application publicly

## Configure environment

Copy the template and replace every placeholder with deployment values:

```powershell
Copy-Item .env.example .env
```

Required variables are `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `FRONTEND_URL`,
`ALLOWED_ORIGINS`, and `API_URL` when running the JAR directly. Compose also
requires `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD`.

Optional variables include `PORT`, `JPA_DDL_AUTO`, `UPLOAD_DIR`,
`APP_AUDIT_TRUSTED_PROXIES`, `SESSION_TIMEOUT`, `COOKIE_SAME_SITE`,
`COOKIE_SECURE`, `SPRINGDOC_ENABLED`, and `SHUTDOWN_TIMEOUT`. Keep
`JPA_DDL_AUTO=validate` in production.

## Build and run with Docker Compose

```powershell
docker compose --env-file .env build
docker compose --env-file .env up -d
docker compose ps
```

The application is published on the configured host `PORT` (default `8080`).
The database data and application uploads are stored in named Docker volumes.

## Build and run the JAR

```powershell
.\mvnw.cmd clean package
java -jar .\target\ramafest.jar --spring.profiles.active=prod
```

## Check health

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

A healthy deployment returns HTTP 200 with a JSON status of `UP`. Put the
application behind HTTPS and a reverse proxy before exposing it to the public
internet.

## VPS deployment with GitHub Actions

The repository uses a Linux VPS deployment target. The `Docker image` workflow
publishes a private image to GHCR on pushes to `main` and version tags. The
`Deploy to VPS` workflow runs after a successful image publish, connects over
SSH, pulls the commit-SHA image, restarts Compose, and verifies
`/actuator/health` reports `UP`.

### GitHub Actions secrets

Add these under **Settings > Secrets and variables > Actions > Secrets**:

- `SSH_HOST`: VPS hostname or IP address.
- `SSH_USER`: non-root SSH user with permission to run Docker.
- `SSH_KEY`: private key for that user.
- `SSH_KNOWN_HOSTS`: output for the VPS from `ssh-keyscan -H your-host`.
- `DEPLOY_URL`: public base URL, such as `https://api.example.com`, without a trailing slash.

The Docker workflow uses the automatically provided `GITHUB_TOKEN`; no
additional registry secret is required in GitHub. The package must remain
private in the repository/package settings.

### Prepare the VPS

Install Docker Engine and the Compose plugin using Docker's official
instructions for your Linux distribution. Then create a dedicated deployment
directory and copy the Compose file and environment file:

```bash
sudo mkdir -p /opt/ramafest
sudo chown "$USER":"$USER" /opt/ramafest
cd /opt/ramafest
curl -fsSL -o docker-compose.yml https://raw.githubusercontent.com/kuldeepkaashyapp/RamaFestBackend/main/docker-compose.yml
cp .env.example .env
chmod 600 .env
```

Edit `.env` on the VPS and set real values for the database, URLs, and
operations settings. Also set `GHCR_PULL_USERNAME` and a GitHub token with
read-only package access in this server-side file. Never commit this `.env`.
Keep `IMAGE_NAME=ghcr.io/kuldeepkaashyapp/ramafest` and let the workflow
provide `IMAGE_TAG`.

Create a non-root deployment user, grant only the Docker permission needed by
the deployment, and install its public key in `~/.ssh/authorized_keys`. Open
only SSH (preferably restricted to administrative source IPs) and HTTP/HTTPS:

```bash
sudo ufw allow OpenSSH
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

Do not expose PostgreSQL or the application port directly to the internet.

### Nginx and free HTTPS

Install Nginx and Certbot with the Nginx plugin from your distribution
packages. Point DNS for the deployment hostname at the VPS, configure Nginx
to proxy to `http://127.0.0.1:8080`, and request a certificate:

```bash
sudo apt-get update
sudo apt-get install -y nginx certbot python3-certbot-nginx
sudo certbot --nginx -d api.example.com
sudo systemctl enable --now nginx
sudo certbot renew --dry-run
```

Use the hostname from `DEPLOY_URL` and keep the application bound behind Nginx.
Certbot configures renewal; verify renewal periodically.

### Roll back

Find the previous image SHA in the GitHub Actions run history, then on the VPS:

```bash
cd /opt/ramafest
IMAGE_TAG=<previous-commit-sha> docker compose pull app
IMAGE_TAG=<previous-commit-sha> docker compose up -d --no-build
curl --fail https://api.example.com/actuator/health
```

Replace the placeholder with the last known-good SHA. Do not use `latest` for
an emergency rollback when an immutable SHA is available.

### Back up and restore PostgreSQL

Back up the database before upgrades and store the dump outside the VPS:

```bash
cd /opt/ramafest
set -a; . ./.env; set +a
docker compose exec -T db pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
  | gzip > "ramafest-$(date +%F-%H%M%S).sql.gz"
```

Restore only during a planned maintenance window:

```bash
gunzip -c ramafest-YYYY-MM-DD-HHMMSS.sql.gz \
  | docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
```

Test restores on a separate database or host. Named Docker volumes are not a
backup strategy by themselves; retain encrypted, access-controlled copies and
document the recovery procedure.
