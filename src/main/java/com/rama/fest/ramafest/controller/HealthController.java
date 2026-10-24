package com.rama.fest.ramafest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Service availability endpoints")
public class HealthController {

    @GetMapping({ "/health"})
    @Operation(summary = "Check server health", description = "Returns the current availability status of the API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Server is healthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.status(OK).body(Map.of(
                "message", "Server is healthy",
                "status", true,
                "data", "UP"
        ));
    }
}
