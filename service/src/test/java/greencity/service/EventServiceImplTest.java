package greencity.service;

import greencity.dto.event.EventDateInfoRequestDto;
import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import greencity.repository.EventRepo;
import greencity.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventRequestDto eventRequestDto;
    private EventResponseDto eventResponseDto;

    @BeforeEach
    void setUp() {
        eventRequestDto = new EventRequestDto();
        eventRequestDto.setTitle("Test Event");
        eventRequestDto.setDescription("This is a test event.");
        eventRequestDto.setEventDays(List.of(new EventDateInfoRequestDto(), new EventDateInfoRequestDto()));

        event = new Event();
        event.setId(1L);
        event.setTitle(eventRequestDto.getTitle());
        event.setDescription(eventRequestDto.getDescription());
        event.setCreationDate(ZonedDateTime.now());

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(event.getId());
        eventResponseDto.setTitle(event.getTitle());
        eventResponseDto.setDescription(event.getDescription());
    }

    @Test
    void createEvent_ShouldSaveAndReturnEventResponseDto() {
        when(modelMapper.map(eventRequestDto, Event.class)).thenReturn(event);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.createEvent(eventRequestDto);

        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getDescription(), result.getDescription());

        verify(eventRepo, times(1)).save(event);
    }

    @Test
    void getEventById_ShouldReturnEventResponseDto_WhenEventExists() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        Optional<EventResponseDto> result = eventService.getEventById(1L);

        assertTrue(result.isPresent());
        assertEquals(event.getId(), result.get().getId());
        verify(eventRepo, times(1)).findById(1L);
    }

    @Test
    void getEventById_ShouldReturnEmpty_WhenEventDoesNotExist() {
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<EventResponseDto> result = eventService.getEventById(1L);

        assertTrue(result.isEmpty());
        verify(eventRepo, times(1)).findById(1L);
    }

    @Test
    void getAllEvents_ShouldReturnListOfEventResponseDto() {
        when(eventRepo.findAll()).thenReturn(List.of(event));
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        List<EventResponseDto> result = eventService.getAllEvents();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(eventRepo, times(1)).findAll();
    }

    @Test
    void createEvent_ShouldThrowIllegalArgumentException_WhenEventDaysAreEmpty() {
        eventRequestDto.setEventDays(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(eventRequestDto);
        });

        assertEquals("Event must have at least one event day.", exception.getMessage());
    }

}
