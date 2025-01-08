package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.service.LanguageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageController languageController;

    private static final String LANGUAGE_PATH = "/language";

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController).build();
        mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should_Return_Language_Codes")
    void getAllLanguageCodes() throws Exception {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("ua", "en"));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(LANGUAGE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(languageService.findAllLanguageCodes()))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Should_Return_Empty_List_When_No_Language_Codes_Present_In_DB")
    void getEmptyListOfLanguageCodes() throws Exception {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(LANGUAGE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(languageService.findAllLanguageCodes()))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").value("<List/>"));
    }


}