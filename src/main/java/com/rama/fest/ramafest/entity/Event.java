package com.rama.fest.ramafest.entity;

import com.rama.fest.ramafest.config.AppConstants;
import com.rama.fest.ramafest.config.AppConstantsConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "events_event_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_title_date",
                        columnNames = {"title", "date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Event category is required")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String category;

    @Size(max = 500, message = "Icon URL cannot exceed 500 characters")
    @Column(length = 500)
    private String icon;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(length = 500)
    private String image;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date cannot be in the past")
    @Column(nullable = false)
    private LocalDateTime date;

    @NotBlank(message = "Venue is required")
    @Size(min = 2, max = 255, message = "Venue must be between 2 and 255 characters")
    @Column(nullable = false, length = 255)
    private String venue;

    @NotNull(message = "Event status is required")
    @Convert(converter = AppConstantsConverter.class)
    @Column(nullable = false, length = 30)
    private AppConstants status;

    @Size(max = 500, message = "Registration form URL cannot exceed 500 characters")
    @Column(name = "registration_form", length = 500)
    private String registrationForm;

    @Column(name = "registration_closes")
    private LocalDateTime registrationCloses;

    @Size(max = 255, message = "Winner name cannot exceed 255 characters")
    @Column(length = 255)
    private String winner;

    @Size(max = 255, message = "Runner-up name cannot exceed 255 characters")
    @Column(name = "runner_up", length = 255)
    private String runnerUp;

    @Size(max = 50, message = "Score cannot exceed 50 characters")
    @Column(length = 50)
    private String score;

    @Size(max = 500, message = "Report link cannot exceed 500 characters")
    @Column(name = "report_link", length = 500)
    private String reportLink;

    @Column(
            name = "is_deleted",
            nullable = false,
            columnDefinition = "boolean default false"
    )
    @Builder.Default
    private boolean isDeleted = false;
}