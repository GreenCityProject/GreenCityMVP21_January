package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
//@WebMvcTest(controllers = {HabitFactController.class})
//@ContextConfiguration(classes = {GreenCityApplication.class})
class HabitFactControllerTest {

    @Mock
    private HabitFactService habitFactService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private HabitFactController habitFactController;

    MockMvc mockMvc;

    private final String FACTS_URL = "/facts";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(habitFactController).build();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getRandomFactByHabitId() {
    }

    @Test
    @DisplayName("Should_Return_Habit_Fact_Of_The_Day")
    void getHabitFactOfTheDay() throws Exception {
        Long languageId = 1L;
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        when(habitFactService.getHabitFactOfTheDay(languageId)).thenReturn(languageTranslationDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/facts/dayFact/%d".formatted(languageId)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(languageTranslationDTO)));

        verify(habitFactService, times(1)).getHabitFactOfTheDay(languageId);
    }

    @Test
    void getAll() throws Exception {
//
//        LanguageTranslationDTO langTransDTOMock = Mockito.mock(LanguageTranslationDTO.class);
//        PageableDto<LanguageTranslationDTO> expected = Mockito.mock(PageableDto.class);
//        expected.setPage(List.of(langTransDTOMock));
//
//        when(habitFactService.getAllHabitFacts(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
//            .thenReturn(expected);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(FACTS_URL).accept(MediaType.APPLICATION_JSON))
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}