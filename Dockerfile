FROM maven:3.9.16-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre

RUN useradd --system --create-home --home-dir /home/ramafest --shell /usr/sbin/nologin ramafest
WORKDIR /app
COPY --from=build /workspace/target/ramafest.jar /app/ramafest.jar
RUN mkdir -p /app/uploads && chown -R ramafest:ramafest /app

USER ramafest
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ramafest.jar"]
