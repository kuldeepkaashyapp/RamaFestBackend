package com.rama.fest.ramafest.service;

import com.rama.fest.ramafest.dto.EventRequestDTO;
import com.rama.fest.ramafest.dto.response.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO e);

    Page<EventResponseDTO> getAllEvent(Pageable pageable);

    EventResponseDTO getEventById(Long id);

    EventResponseDTO updateEvent(Long eventId, EventRequestDTO eventDTO);

    EventResponseDTO deleteEventById(Long id);
}
