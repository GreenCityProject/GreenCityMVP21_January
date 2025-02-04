package greencity.controller;

import greencity.dto.event.EventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private EventResponseDto eventResponseDto;
    private Principal principal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();

        eventResponseDto = EventResponseDto.builder()
                .title("Sample Event")
                .description("This is a sample event description.")
                .build();

        principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
    }

    @Test
    void testCreateEvent_Success() throws Exception {

        when(eventService.createEvent(any(EventRequestDto.class))).thenReturn(eventResponseDto);

        String eventRequestJson = "{\n" +
                "  \"title\": \"Green Cleanup Event\",\n" +
                "  \"description\": \"Join us for a citywide cleanup event to promote environmental sustainability.\",\n" +
                "  \"duration\": 2,\n" +
                "  \"eventDays\": [\n" +
                "    {\n" +
                "      \"eventDate\": \"2025-03-15\",\n" +
                "      \"eventTimeStart\": \"2025-03-15T10:00:00\",\n" +
                "      \"eventTimeEnd\": \"2025-03-15T14:00:00\",\n" +
                "      \"isAllDay\": false,\n" +
                "      \"isPlace\": true,\n" +
                "      \"isOnline\": false,\n" +
                "      \"location\": \"Central Park, Main Street\",\n" +
                "      \"url\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"eventDate\": \"2025-03-16\",\n" +
                "      \"eventTimeStart\": \"2025-03-16T11:00:00\",\n" +
                "      \"eventTimeEnd\": \"2025-03-16T15:00:00\",\n" +
                "      \"isAllDay\": false,\n" +
                "      \"isPlace\": true,\n" +
                "      \"isOnline\": false,\n" +
                "      \"location\": \"West Park, Green Avenue\",\n" +
                "      \"url\": null\n" +
                "    }\n" +
                "  ],\n" +
                "  \"initiativeTypes\": [\n" +
                "    {\n" +
                "      \"name\": \"Economic\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"isOpen\": true,\n" +
                "  \"images\": [\n" +
                "    {\n" +
                "      \"imagePath\": \"https://azurestorage.com/event-images/cleanup7.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"imagePath\": \"https://azurestorage.com/event-images/cleanup8.jpg\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"mainImage\": {\n" +
                "    \"imagePath\": \"https://azurestorage.com/event-images/cleanup7.jpg\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventRequestJson)
                        .principal(principal))
                .andExpect(status().isCreated());
    }


    @Test
    void testCreateEvent_InvalidRequest() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }
}
