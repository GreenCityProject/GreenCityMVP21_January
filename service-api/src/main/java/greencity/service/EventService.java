package greencity.service;

import greencity.dto.event.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventResponseDto createEvent(EventRequestDto eventRequestDto);

    EventResponseDto updateEvent(Long id, EventUpdateDto eventUpdateDto, String userEmail);

    void deleteEvent(Long id);

    Optional<EventResponseDto> getEventById(Long id, String userEmail);

    EventProfilePreviewPageable getEventsByTitle(String title, Pageable pageable);

    List<EventResponseDto> getAllEvents();

    EventProfilePreviewPageable getAllUserEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserEventsByStatus(String userEmail, String status, Pageable pageable);

    EventProfilePreviewPageable getAllUserPastEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserLiveEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllUserUpcomingEvents(String userEmail, Pageable pageable);

    EventProfilePreviewPageable getAllEventsPageable(Pageable pageable);
}
