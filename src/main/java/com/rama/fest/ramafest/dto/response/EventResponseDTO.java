package com.rama.fest.ramafest.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponseDTO {

    private Long id;
    private String title;
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
