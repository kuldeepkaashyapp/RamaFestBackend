package com.rama.fest.ramafest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ramaFestOpenApi(@Value("${spring.api.url}") String apiUrl) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("sessionCookie", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Session cookie returned after a successful login.")))
                .info(new Info()
                        .title("RamaFest API")
                        .version("v1")
                        .description("REST API for authentication, event management, and server health monitoring.")
                        .contact(new Contact()
                                .name("RamaFest Development Team"))
                        .license(new License()
                                .name("Internal Use")))
                .addServersItem(new Server()
                        .url(normalizeApiUrl(apiUrl))
                        .description("RamaFest API"));
    }

    private String normalizeApiUrl(String apiUrl) {
        String normalizedUrl = apiUrl == null ? "" : apiUrl.trim();
        if (!normalizedUrl.startsWith("http://")
                && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "http://" + normalizedUrl;
        }

        URI uri = URI.create(normalizedUrl);
        if (uri.getHost() == null) {
            throw new IllegalArgumentException(
                    "API_URL must be a valid HTTP or HTTPS URL, for example http://localhost:8981");
        }

        return normalizedUrl.replaceAll("/+$", "");
    }
}
