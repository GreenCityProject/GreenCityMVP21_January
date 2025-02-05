package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.*;
import greencity.entity.Event;
import greencity.entity.EventDateInfo;
import greencity.entity.Image;
import greencity.entity.InitiativeType;
import greencity.repository.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    private InitiativeTypeRepo initiativeTypeRepo;

    @Mock
    private EventDateInfoRepo eventDateInfoRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ImageRepo imageRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventRequestDto eventRequestDto;
    private EventResponseDto eventResponseDto;
    private InitiativeType initiativeType;

    @BeforeEach
    void setUp() {
        eventRequestDto = new EventRequestDto();
        eventRequestDto.setTitle("Test Event");
        eventRequestDto.setDescription("This is a test event.");
        eventRequestDto.setEventDays(List.of(new EventDateInfoRequestDto(), new EventDateInfoRequestDto()));

        ImageRequestDto imageRequestDto = new ImageRequestDto();
        imageRequestDto.setImagePath("ImagePath");

        event = new Event();
        event.setId(1L);
        event.setTitle(eventRequestDto.getTitle());
        event.setDescription(eventRequestDto.getDescription());
        event.setCreationDate(ZonedDateTime.now());
        event.setImages(Set.of(new Image()));
        event.setAuthor(ModelUtils.getUser());

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(event.getId());
        eventResponseDto.setTitle(event.getTitle());
        eventResponseDto.setDescription(event.getDescription());

        initiativeType = new InitiativeType();
        initiativeType.setId(1L);
        initiativeType.setName("Test Initiative Type");

        InitiativeTypeRequestDto initiativeTypeRequestDto = new InitiativeTypeRequestDto();
        initiativeTypeRequestDto.setName("Test Initiative Type");

        eventRequestDto.setInitiativeTypes(List.of(initiativeTypeRequestDto));
        eventRequestDto.setImages(List.of(imageRequestDto));
        eventRequestDto.setAuthorEmail(ModelUtils.getUser().getEmail());

        EventDateInfoRequestDto eventDateInfoRequestDto = new EventDateInfoRequestDto();
        eventDateInfoRequestDto.setIsOnline(true);
        eventDateInfoRequestDto.setUrl("http://google.com");
        eventDateInfoRequestDto.setIsPlace(false);

        eventRequestDto.setEventDays(List.of(eventDateInfoRequestDto));

    }

    @Test
    void createEvent_ShouldSaveAndReturnEventResponseDto() throws MessagingException {
        when(modelMapper.map(eventRequestDto, Event.class)).thenReturn(event);
        when(initiativeTypeRepo.findByName(any(String.class))).thenReturn(Optional.of(initiativeType));
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);
        when(userRepo.findByEmail(any(String.class))).thenReturn(Optional.of(ModelUtils.getUser()));
        when(imageRepo.findByImagePath(any(String.class))).thenReturn(Optional.of(new Image()));
        when(modelMapper.map(any(EventDateInfoRequestDto.class), eq(EventDateInfo.class))).thenReturn(new EventDateInfo());
        when(eventDateInfoRepo.save(any(EventDateInfo.class))).thenReturn(new EventDateInfo());
        when(modelMapper.map(any(ImageRequestDto.class), eq(Image.class))).thenReturn(new Image());

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
