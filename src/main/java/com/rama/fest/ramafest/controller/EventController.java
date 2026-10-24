package com.rama.fest.ramafest.controller;


import com.rama.fest.ramafest.dto.EventRequestDTO;
import com.rama.fest.ramafest.dto.response.EventResponseDTO;
import com.rama.fest.ramafest.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@CrossOrigin
@RequestMapping("/api/event")
@Tag(name = "Events", description = "Event management endpoints")
@SecurityRequirement(name = "sessionCookie")
public class EventController {

    @Autowired
    EventService eventService;

    Logger logger = LoggerFactory.getLogger(EventController.class);




    @PostMapping("/create")
    @Operation(summary = "Create an event", description = "Creates a new event.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event created"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDTO e){


        EventResponseDTO response=eventService.createEvent(e);

        logger.info("EventResponse",response);

        return new ResponseEntity<>(response,HttpStatus.OK);


    }
    @GetMapping("/events")
    @Operation(summary = "List events", description = "Returns a paginated list of events sorted by date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Page<EventResponseDTO>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        try {

            // Validate page
            if (page < 0) {
                page = 0;
            }

            // Validate size
            if (size <= 0) {
                size = 10;
            }

            // Prevent very large requests
            if (size > 100) {
                size = 100;
            }

            // Allow only valid sorting field
            if (!sortBy.equals("date")) {
                sortBy = "date";
            }

            Sort.Direction sortDirection =
                    direction.equalsIgnoreCase("asc")
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(sortDirection, sortBy)
            );

            Page<EventResponseDTO> responseDTOPage =
                    eventService.getAllEvent(pageable);

            return ResponseEntity.ok(responseDTOPage);

        } catch (Exception e) {

            logger.error("Error while fetching events", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event", description = "Returns an event by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> getEventById(
            @Parameter(description = "Event identifier", example = "1", required = true)
            @PathVariable Long id){

        EventResponseDTO responseDTO = eventService.getEventById(id);

        logger.info("EventResponse: {}", responseDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event", description = "Soft-deletes an event. The record remains in the database but is excluded from normal event responses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> deleteEvent(
            @Parameter(description = "Event identifier", example = "1", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(eventService.deleteEventById(id));
    }




    @RequestMapping(
            value = {"/{eventId}", "/patch{eventId}"},
            method = {RequestMethod.PATCH, RequestMethod.PUT}
    )
    @Operation(summary = "Update an event", description = "Updates an existing event using PUT or PATCH.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> patchEvent(
            @Parameter(description = "Event identifier", example = "1", required = true)
            @PathVariable Long eventId,
            @RequestBody EventRequestDTO patchRequest) {
        return ResponseEntity.ok(
                eventService.updateEvent(eventId, patchRequest)
        );
    }


}
