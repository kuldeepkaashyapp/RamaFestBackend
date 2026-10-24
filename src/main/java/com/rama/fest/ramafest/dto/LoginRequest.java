package com.rama.fest.ramafest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class LoginRequest {

    @Schema(description = "Unique username", example = "rama.user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "User password", example = "StrongPassword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    @NotBlank(message = "Password is required")
    private String password;
}