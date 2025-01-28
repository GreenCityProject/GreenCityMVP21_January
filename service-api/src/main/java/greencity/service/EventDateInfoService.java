package greencity.service;

import greencity.dto.event.EventDateInfoRequestDto;
import greencity.dto.event.EventDateInfoResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface EventDateInfoService {

    EventDateInfoResponseDto createEventDateInfo(Long eventId, EventDateInfoRequestDto requestDto);

    EventDateInfoResponseDto updateEventDateInfo(Long id, EventDateInfoRequestDto requestDto);

    void deleteEventDateInfo(Long id);

    List<EventDateInfoResponseDto> getEventDateInfoByEvent(Long eventId);

    List<EventDateInfoResponseDto> getEventDateInfoByDateRange(LocalDate startDate, LocalDate endDate);

    List<EventDateInfoResponseDto> getAllOnlineEvents();

    List<EventDateInfoResponseDto> getAllOfflineEvents();

    List<EventDateInfoResponseDto> getEventsByLocation(String location);

    List<EventDateInfoResponseDto> getEventsByUrl(String url);

}
