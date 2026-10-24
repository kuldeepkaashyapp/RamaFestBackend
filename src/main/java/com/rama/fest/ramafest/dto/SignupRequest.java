package com.rama.fest.ramafest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Getter
@Setter
@ToString

public class SignupRequest {

    @Schema(description = "Username for the new account", example = "rama.user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Password for the new account", example = "StrongPassword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    @NotBlank(message = "Password is required")
    private String password;

}
