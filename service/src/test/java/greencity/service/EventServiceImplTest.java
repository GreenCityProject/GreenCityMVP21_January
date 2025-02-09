package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.*;
import greencity.entity.*;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EventMappingContext;
import greencity.repository.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
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

    @Mock
    private ParticipationRepo participationRepo;

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
        eventResponseDto.setJoined(true);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        Optional<EventResponseDto> result = eventService.getEventById(1L, "email@gmail.com");

        assertTrue(result.isPresent());
        assertEquals(event.getId(), result.get().getId());
        assertTrue(result.get().isJoined());
        verify(eventRepo, times(1)).findById(1L);
    }

    @Test
    void getEventByIdNotFound() {
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getEventById(1L, "email@gmail.com"));
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

    @Test
    void getAllUserEventsTest() {
        User mockUser = ModelUtils.getUser();
        when(userRepo.findByEmail(any(String.class))).thenReturn(Optional.of(mockUser));

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        List<Event> content = List.of(event1, event2);
        Page<Event> events = new PageImpl<>(content);
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

        when(eventRepo.findAllByAuthorOrParticipant(anyLong(), eq(pageable))).thenReturn(events);
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo()));
        when(participationRepo.findUsersByEventId(anyLong())).thenReturn(List.of());

        EventProfilePreviewDto mockDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Mock Event Dto")
                .build();

        when(modelMapper.map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class)))
                .thenReturn(mockDto);

        EventProfilePreviewPageable result = eventService.getAllUserEvents(mockUser.getEmail(), pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Mock Event Dto", result.getContent().get(0).getTitle());

        verify(userRepo, times(1)).findByEmail(anyString());
        verify(eventRepo, times(1)).findAllByAuthorOrParticipant(anyLong(), eq(pageable));
        verify(eventDateInfoRepo, times(2)).findByEvent(any(Event.class));
        verify(participationRepo, times(2)).findUsersByEventId(anyLong());
        verify(modelMapper, times(2)).map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class));
    }

    @Test
    void getAllUserEventsNoUserFoundTest() {
        when(userRepo.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getAllUserEvents("a@gmail.com", Pageable.unpaged()));

        verify(userRepo, times(1)).findByEmail(anyString());
        verify(eventRepo, never()).findAllByAuthorOrParticipant(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllUserPastEvents_ShouldReturnEventProfilePreviewPageable() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        List<Event> content = List.of(event1, event2);
        Page<Event> events = new PageImpl<>(content);
        Pageable pageable = PageRequest.of(0, 3);

        when(eventRepo.findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("PAST"), eq(pageable))).thenReturn(events);
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo()));
        when(participationRepo.findUsersByEventId(anyLong())).thenReturn(List.of());

        EventProfilePreviewDto mockDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Mock Past Event Dto")
                .build();
        when(modelMapper.map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class)))
                .thenReturn(mockDto);

        EventProfilePreviewPageable result = eventService.getAllUserPastEvents(userEmail, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Mock Past Event Dto", result.getContent().get(0).getTitle());

        verify(userRepo, times(1)).findByEmail(userEmail);
        verify(eventRepo, times(1)).findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("PAST"), eq(pageable));
    }

    @Test
    void getAllUserLiveEvents_ShouldReturnEventProfilePreviewPageable() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        List<Event> content = List.of(event1, event2);
        Page<Event> events = new PageImpl<>(content);
        Pageable pageable = PageRequest.of(0, 3);

        when(eventRepo.findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("LIVE"), eq(pageable))).thenReturn(events);
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo()));
        when(participationRepo.findUsersByEventId(anyLong())).thenReturn(List.of());

        EventProfilePreviewDto mockDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Mock Live Event Dto")
                .build();
        when(modelMapper.map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class)))
                .thenReturn(mockDto);

        EventProfilePreviewPageable result = eventService.getAllUserLiveEvents(userEmail, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Mock Live Event Dto", result.getContent().get(0).getTitle());

        verify(userRepo, times(1)).findByEmail(userEmail);
        verify(eventRepo, times(1)).findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("LIVE"), eq(pageable));
    }

    @Test
    void getAllUserUpcomingEvents_ShouldReturnEventProfilePreviewPageable() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        List<Event> content = List.of(event1, event2);
        Page<Event> events = new PageImpl<>(content);
        Pageable pageable = PageRequest.of(0, 3);

        when(eventRepo.findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("UPCOMING"), eq(pageable))).thenReturn(events);
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo()));
        when(participationRepo.findUsersByEventId(anyLong())).thenReturn(List.of());

        EventProfilePreviewDto mockDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Mock Upcoming Event Dto")
                .build();
        when(modelMapper.map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class)))
                .thenReturn(mockDto);

        EventProfilePreviewPageable result = eventService.getAllUserUpcomingEvents(userEmail, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Mock Upcoming Event Dto", result.getContent().get(0).getTitle());

        verify(userRepo, times(1)).findByEmail(userEmail);
        verify(eventRepo, times(1)).findUserEventsByTime(eq(user.getId()), any(LocalDateTime.class), eq("UPCOMING"), eq(pageable));
    }

    @Test
    void getAllUserEventsByStatus_ShouldReturnEventProfilePreviewPageable() {
        String userEmail = "user@example.com";
        User user = new User();
        user.setId(1L);
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        EventDateInfo eventDateInfo = new EventDateInfo();
        eventDateInfo.setId(1L);
        eventDateInfo.setEvent(event1);

        EventDateInfo eventDateInfo2 = new EventDateInfo();
        eventDateInfo2.setId(2L);
        eventDateInfo2.setEvent(event2);

        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(eventDateInfo));

        List<Event> content = List.of(event1, event2);
        Page<Event> events = new PageImpl<>(content);
        Pageable pageable = PageRequest.of(0, 3);

        boolean isOnline = true;
        when(eventRepo.findEventsByAuthorAndFirstDayOnlineStatus(eq(user.getId()), eq(isOnline), eq(pageable)))
                .thenReturn(events);

        EventProfilePreviewDto mockDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Mock Event Dto")
                .build();
        when(modelMapper.map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class)))
                .thenReturn(mockDto);

        EventProfilePreviewPageable result = eventService.getAllUserEventsByStatus(userEmail, "online", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Mock Event Dto", result.getContent().get(0).getTitle());

        verify(userRepo, times(1)).findByEmail(userEmail);
        verify(eventRepo, times(1)).findEventsByAuthorAndFirstDayOnlineStatus(eq(user.getId()), eq(isOnline), eq(pageable));
        verify(eventDateInfoRepo, times(2)).findByEvent(any(Event.class));
        verify(participationRepo, times(2)).findUsersByEventId(anyLong());
        verify(modelMapper, times(2)).map(any(EventMappingContext.class), eq(EventProfilePreviewDto.class));
    }
}
