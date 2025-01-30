package greencity.service;

import greencity.dto.event.EventDateInfoRequestDto;
import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import greencity.mapping.EventRequestDtoMapper;
import greencity.mapping.EventResponseDtoMapper;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        if (eventRequestDto.getEventDays() == null || eventRequestDto.getEventDays().isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one event day.");
        }

        Event event = modelMapper.map(eventRequestDto, Event.class);
        event.setCreationDate(ZonedDateTime.now());

        Event savedEvent = eventRepo.save(event);
        return modelMapper.map(savedEvent, EventResponseDto.class);
    }

    @Override
    public EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto) {
        return null;
    }

    @Override
    public void deleteEvent(Long id) {

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventResponseDto> getEventById(Long id) {
        return eventRepo.findById(id)
                .map(event -> modelMapper.map(event, EventResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDto> findEventsByTitle(String title) {
        return List.of();
    }

    @Override
    public List<EventResponseDto> getAllOpenEvents() {
        return List.of();
    }
}
