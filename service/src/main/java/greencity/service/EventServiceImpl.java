package greencity.service;

import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.InitiativeTypeRequestDto;
import greencity.entity.Event;
import greencity.entity.InitiativeType;
import greencity.repository.EventRepo;
import greencity.repository.InitiativeTypeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final InitiativeTypeRepo initiativeTypeRepo;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        if (eventRequestDto.getEventDays() == null || eventRequestDto.getEventDays().isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one event day.");
        }

        Event event = modelMapper.map(eventRequestDto, Event.class);
        event.setCreationDate(ZonedDateTime.now());

        List<InitiativeType> initiativeTypes = new ArrayList<>();
        for (InitiativeTypeRequestDto i : eventRequestDto.getInitiativeTypes()) {
            initiativeTypes.add(initiativeTypeRepo.findByName(i.getName()).get());
        }

        event.setInitiativeTypes(initiativeTypes);

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
