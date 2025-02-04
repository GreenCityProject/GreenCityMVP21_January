package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.*;
import greencity.dto.user.AuthorDto;
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
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;

    private Principal principal = getPrincipal();

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

    private Map<String, Object> attributes;
    private EventRequestDto eventRequestDto;
    private EventDateInfoRequestDto eventDateInfoRequestDto;
    private ObjectMapper objectMapper2;
    private MockMvc mockMvc;
    private EventResponseDto eventResponseDto;

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

        eventDateInfoRequestDto = new EventDateInfoRequestDto();
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
}
