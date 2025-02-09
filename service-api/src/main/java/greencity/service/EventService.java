package greencity.service;

import greencity.dto.event.EventProfilePreviewPageable;
import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventResponseDto createEvent(EventRequestDto eventRequestDto);

    EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto);

    void deleteEvent(Long id);

    Optional<EventResponseDto> getEventById(Long id, String userEmail);

    List<EventResponseDto> getAllEvents();

    EventProfilePreviewPageable getAllUserEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserEventsByStatus(String userEmail, String status, Pageable pageable);

    EventProfilePreviewPageable getAllUserPastEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserLiveEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserUpcomingEvents(String userEmail, Pageable pageable);

}
