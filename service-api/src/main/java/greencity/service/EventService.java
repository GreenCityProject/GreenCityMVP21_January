package greencity.service;

import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventResponseDto createEvent(EventRequestDto eventRequestDto);

    EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto);

    void deleteEvent(Long id);

    Optional<EventResponseDto> getEventById(Long id);

    List<EventResponseDto> getAllEvents();

    List<EventResponseDto> findEventsByTitle(String title);

    List<EventResponseDto> getAllOpenEvents();

}
