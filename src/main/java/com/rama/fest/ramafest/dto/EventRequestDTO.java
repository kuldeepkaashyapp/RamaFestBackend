package com.rama.fest.ramafest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDTO {

    @Schema(description = "Event title", example = "Rama Fest Hackathon", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Event category", example = "Technology", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category is required")
    private String category;

    private String icon;

    private String image;

    private String description;

    private LocalDateTime date;

    private String venue;



    private String status;

    private String registrationForm;

    private LocalDateTime registrationCloses;

    private String winner;

    private String runnerUp;

    private String score;

    private String reportLink;
}
