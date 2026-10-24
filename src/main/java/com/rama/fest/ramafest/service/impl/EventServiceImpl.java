package com.rama.fest.ramafest.service.impl;


import com.rama.fest.ramafest.config.AppConstants;
import com.rama.fest.ramafest.dto.EventRequestDTO;
import com.rama.fest.ramafest.dto.response.EventResponseDTO;
import com.rama.fest.ramafest.entity.Event;
import com.rama.fest.ramafest.exceptions.ResourceNotFoundException;
import com.rama.fest.ramafest.reposatory.EventRepostory;
import com.rama.fest.ramafest.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepostory eventRepository;

    @Autowired
    private ModelMapper modelMapper;








    @Override
    public EventResponseDTO createEvent(EventRequestDTO e) {

        Event event = modelMapper.map(e, Event.class);
        event.setStatus(AppConstants.fromValue(e.getStatus()));
        Event responseDTO = eventRepository.save(event);


        //saving Response

        return modelMapper.map(responseDTO, EventResponseDTO.class);

    }

    @Override
    public Page<EventResponseDTO> getAllEvent(Pageable pageable) {
        Page<Event> events = eventRepository.findAllActive(pageable);

        return events.map(event -> modelMapper.map(event, EventResponseDTO.class));
    }
    @Override
    public EventResponseDTO getEventById(Long id) {

        Event responseDTO = eventRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        return modelMapper.map(responseDTO, EventResponseDTO.class);
    }



    @Override
    public EventResponseDTO updateEvent(Long eventId, EventRequestDTO eventDTO) {
        log.info("Updating event with ID: {}", eventId);

        Event event = eventRepository.findActiveById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + eventId
                        ));

        updateOnlyProvidedFields(event, eventDTO);

        Event updatedEvent = eventRepository.save(event);
        return modelMapper.map(updatedEvent, EventResponseDTO.class);
    }

    private void updateOnlyProvidedFields(Event event, EventRequestDTO eventDTO) {
        if (eventDTO.getTitle() != null) {
            event.setTitle(eventDTO.getTitle());
        }
        if (eventDTO.getCategory() != null) {
            event.setCategory(eventDTO.getCategory());
        }
        if (eventDTO.getIcon() != null) {
            event.setIcon(eventDTO.getIcon());
        }
        if (eventDTO.getImage() != null) {
            event.setImage(eventDTO.getImage());
        }
        if (eventDTO.getDescription() != null) {
            event.setDescription(eventDTO.getDescription());
        }
        if (eventDTO.getDate() != null) {
            event.setDate(eventDTO.getDate());
        }
        if (eventDTO.getVenue() != null) {
            event.setVenue(eventDTO.getVenue());
        }
        if (eventDTO.getStatus() != null) {
            event.setStatus(AppConstants.fromValue(eventDTO.getStatus()));
        }
        if (eventDTO.getRegistrationForm() != null) {
            event.setRegistrationForm(eventDTO.getRegistrationForm());
        }
        if (eventDTO.getRegistrationCloses() != null) {
            event.setRegistrationCloses(eventDTO.getRegistrationCloses());
        }
        if (eventDTO.getWinner() != null) {
            event.setWinner(eventDTO.getWinner());
        }
        if (eventDTO.getRunnerUp() != null) {
            event.setRunnerUp(eventDTO.getRunnerUp());
        }
        if (eventDTO.getScore() != null) {
            event.setScore(eventDTO.getScore());
        }
        if (eventDTO.getReportLink() != null) {
            event.setReportLink(eventDTO.getReportLink());
        }
    }



    @Override
    public EventResponseDTO deleteEventById(Long id) {

        Event event = eventRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        event.setDeleted(true);

        Event updatedEvent = eventRepository.save(event);

        return modelMapper.map(updatedEvent, EventResponseDTO.class);
    }





}
