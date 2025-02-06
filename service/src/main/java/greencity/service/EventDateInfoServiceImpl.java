package greencity.service;

import greencity.dto.event.EventDateInfoRequestDto;
import greencity.dto.event.EventDateInfoResponseDto;
import greencity.entity.Event;
import greencity.entity.EventDateInfo;
import greencity.repository.EventDateInfoRepo;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventDateInfoServiceImpl implements EventDateInfoService {

    private final EventDateInfoRepo eventDateInfoRepo;
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;

    @Override
    public EventDateInfoResponseDto createEventDateInfo(Long eventId, EventDateInfoRequestDto requestDto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        EventDateInfo eventDateInfo = modelMapper.map(requestDto, EventDateInfo.class);
        eventDateInfo.setEvent(event);

        EventDateInfo savedEventDateInfo = eventDateInfoRepo.save(eventDateInfo);

        return modelMapper.map(savedEventDateInfo, EventDateInfoResponseDto.class);
    }

    @Override
    public EventDateInfoResponseDto updateEventDateInfo(Long id, EventDateInfoRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteEventDateInfo(Long id) {

    }

    @Override
    public List<EventDateInfoResponseDto> getEventDateInfoByEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        List<EventDateInfo> eventDateInfos = eventDateInfoRepo.findByEvent(event);

        return eventDateInfos.stream()
                .map(eventDateInfo -> modelMapper.map(eventDateInfo, EventDateInfoResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDateInfoResponseDto> getEventDateInfoByDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<EventDateInfoResponseDto> getAllOnlineEvents() {
        return List.of();
    }

    @Override
    public List<EventDateInfoResponseDto> getAllOfflineEvents() {
        return List.of();
    }

    @Override
    public List<EventDateInfoResponseDto> getEventsByLocation(String location) {
        return List.of();
    }

    @Override
    public List<EventDateInfoResponseDto> getEventsByUrl(String url) {
        return List.of();
    }
}

