package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.*;
import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;

    private final Principal principal = getPrincipal();

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventController eventController;

    private EventRequestDto eventRequestDto;
    private ObjectMapper objectMapper2;
    private MockMvc mockMvc;
    private EventResponseDto eventResponseDto;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();

        attributes.put("timestamp", "2025-01-13T10:00:00");
        attributes.put("trace", "Test stack trace");

        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();

        objectMapper2 = new ObjectMapper();
        objectMapper2.registerModule(new JavaTimeModule());
        objectMapper2.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        eventRequestDto = new EventRequestDto();
        eventRequestDto.setAuthorEmail(principal.getName());
        eventRequestDto.setMainImage(ImageRequestDto.builder().imagePath("imagePath").build());
        eventRequestDto.setTitle("title");
        eventRequestDto.setDescription("descriptionglygluigluyfluyflukyfkytfkytdkytdkytdkytkyt");
        eventRequestDto.setInitiativeTypes(List.of(InitiativeTypeRequestDto.builder().name("Economic").build()));
        eventRequestDto.setOpen(true);
        eventRequestDto.setDuration(1);
        eventRequestDto.setMainImage(ImageRequestDto.builder().imagePath("imagePath").build());

        EventDateInfoRequestDto eventDateInfoRequestDto = new EventDateInfoRequestDto();
        eventDateInfoRequestDto.setIsOnline(true);
        eventDateInfoRequestDto.setIsAllDay(false);
        eventDateInfoRequestDto.setUrl("http://google.com");
        eventDateInfoRequestDto.setIsPlace(false);
        eventDateInfoRequestDto.setEventDate(LocalDate.of(2025, 12, 15));
        eventDateInfoRequestDto.setEventTimeStart(LocalDateTime.of(2025, 12, 15, 14, 30));
        eventDateInfoRequestDto.setEventTimeEnd(LocalDateTime.of(2025, 12, 15, 15, 30));

        eventRequestDto.setEventDays(List.of(eventDateInfoRequestDto));

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(1L);
        eventResponseDto.setTitle("title");
        eventResponseDto.setDescription("descriptionglygluigluyfluyflukyfkytfkytdkytdkytdkytkyt");
        eventResponseDto.setOpen(true);
        eventResponseDto.setDuration(1);
        eventResponseDto.setMainImage(ImageResponseDto.builder().imagePath("imagePath").build());
        eventResponseDto.setAuthor(AuthorDto.builder().name("Masha").id(12L).build());
    }

    @Test
    void createTest() throws Exception {

        when(eventService.createEvent(any(EventRequestDto.class))).thenReturn(eventResponseDto);

        MvcResult result = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper2.writeValueAsString(eventRequestDto))
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(eventResponseDto.getId()))
                .andExpect(jsonPath("$.title").value(eventResponseDto.getTitle()))
                .andExpect(jsonPath("$.description").value(eventResponseDto.getDescription()))
                .andExpect(jsonPath("$.open").value(eventResponseDto.isOpen()))
                .andExpect(jsonPath("$.duration").value(eventResponseDto.getDuration()))
                .andExpect(jsonPath("$.author.name").value(eventResponseDto.getAuthor().getName()))
                .andExpect(jsonPath("$.author.id").value(eventResponseDto.getAuthor().getId()))
                .andExpect(jsonPath("$.mainImage.imagePath").value(eventResponseDto.getMainImage().getImagePath()))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("title"));

        verify(eventService, times(1)).createEvent(any(EventRequestDto.class));
    }

    @Test
    void createTestNoTitle() throws Exception {
        eventRequestDto.setTitle(null);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper2.writeValueAsString(eventRequestDto))
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].message").value("Title cannot be empty"))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andReturn();

        verify(eventService, times(0)).createEvent(any(EventRequestDto.class));
    }

    @Test
    void getEventsByUserTest() throws Exception {
        EventProfilePreviewDto eventProfilePreviewDto = EventProfilePreviewDto.builder()
                .id(1L)
                .title("Sample Event")
                .creationDate(ZonedDateTime.now())
                .eventDate(LocalDate.now().plusDays(5))
                .eventTimeStart(LocalDateTime.now().plusHours(3))
                .author(new AuthorDto(1L, "John Doe"))
                .location("Online")
                .initiativeTypes(List.of(new InitiativeTypeResponseDto(3L, "Economic")))
                .isOpen(true)
                .mainImage(new ImageResponseDto(1L, "https://example.com/image.jpg"))
                .rating(4.5)
                .participants(List.of(new UserProfilePictureDto(1L, "Maria", "https://example.com/user1.jpg")))
                .build();

        EventProfilePreviewDto eventProfilePreviewDto2 = EventProfilePreviewDto.builder()
                .id(2L)
                .title("Eco Conference 2025")
                .creationDate(ZonedDateTime.now().minusDays(2))
                .eventDate(LocalDate.now().plusDays(10))
                .eventTimeStart(LocalDateTime.now().plusHours(5))
                .author(new AuthorDto(2L, "Alice Johnson"))
                .location("Kharkiv")
                .initiativeTypes(List.of(new InitiativeTypeResponseDto(3L, "Economic")))
                .isOpen(false)
                .mainImage(new ImageResponseDto(2L, "https://example.com/event2.jpg"))
                .rating(4.8)
                .participants(List.of(
                        new UserProfilePictureDto(2L, "David", "https://example.com/user2.jpg"),
                        new UserProfilePictureDto(3L, "Sophia", "https://example.com/user3.jpg")
                ))
                .build();

        EventProfilePreviewPageable result = new EventProfilePreviewPageable(
                List.of(eventProfilePreviewDto, eventProfilePreviewDto2),
                0, 3, 10L, 5, false);

        when(eventService.getAllUserEvents(anyString(), any(Pageable.class))).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(get("/events/myEvents")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        EventProfilePreviewPageable response = objectMapper2.readValue(jsonResponse, EventProfilePreviewPageable.class);

        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageNo());
        assertEquals(3, response.getPageSize());
        assertEquals(10L, response.getTotalElements());
        assertEquals(5, response.getTotalPages());
        assertFalse(response.isLast());

        verify(eventService, times(1)).getAllUserEvents(anyString(), any(Pageable.class));
    }

    @Test
    void getEventsByUserNotFoundTest() throws Exception {
        when(eventService.getAllUserEvents(anyString(), any(Pageable.class))).thenThrow(new NotFoundException("User not found: " + ModelUtils.getPrincipal().getName()));

        attributes.put("path", "/events/myEvents");
        attributes.put("message", "User not found: " + ModelUtils.getPrincipal().getName());
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class))).thenReturn(attributes);
        mockMvc.perform(get("/events/myEvents")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id,desc"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
