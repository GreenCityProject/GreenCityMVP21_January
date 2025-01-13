package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.annotations.ValidLanguage;
import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.HabitFactService;
import greencity.service.LanguageService;
import greencity.validator.LanguageTranslationValidator;
import greencity.validator.LanguageValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

//@WebMvcTest(controllers = {HabitFactController.class}) // without 47 throws "Found multiple @SpringBootConfiguration annotated classes"
//@ContextConfiguration(classes = {GreenCityApplication.class}) // with 46 throws "Could not initialize class org.mockito.Mockito"
class HabitFactControllerTest {

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private LanguageService languageService;

    @Mock
    private LanguageValidator languageValidator;

    @Mock
    private LanguageTranslationValidator languageTranslationValidator;

    @Mock
    private ConstraintValidatorContext  constraintValidatorContext;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private HabitFactController habitFactController;

    MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final String HABIT_FACT_CONTROLLER_LINK = "/facts";

    @BeforeEach
    void setUp() throws Exception {
        // 80 thru 83 ara hacks I used to somehow initialize languageService on languageTranslationValidator DOES NOT WORK
        languageTranslationValidator = new LanguageTranslationValidator();
        Field repositoryField = LanguageTranslationValidator.class.getDeclaredField("languageService");
        repositoryField.setAccessible(true);
        repositoryField.set(languageTranslationValidator, languageService);


        when(languageService.getAllLanguages()).thenReturn(List.of(ModelUtils.getLanguageDTO()));
        mockMvc = MockMvcBuilders.standaloneSetup(habitFactController).build();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should_Return_Random_Fact_By_Habit_Id")
    void getRandomFactByHabitId() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        ValidLanguage validLanguage = Mockito.mock(ValidLanguage.class);

        when(languageService.findAllLanguageCodes()).thenReturn(List.of("ua", "en", "fr"));
        doNothing().when(languageValidator).initialize(validLanguage);
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, locale.getLanguage()))
                .thenReturn(languageTranslationDTO);

        /** below throws "Unable to initialize greencity.validator.LanguageValidator"
       Because:  Cannot invoke "greencity.service.LanguageService.findAllLanguageCodes()" because "<local3>.languageService" is null
         **/
        mockMvc.perform(MockMvcRequestBuilders.get("/facts/random/%d".formatted(habitId))
                .accept(MediaType.APPLICATION_JSON)

        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should_Return_Habit_Fact_Of_The_Day")
    void getHabitFactOfTheDay() throws Exception {
        Long languageId = 1L;
        String link = HABIT_FACT_CONTROLLER_LINK.concat("/dayFact/%d".formatted(languageId));
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        when(habitFactService.getHabitFactOfTheDay(languageId)).thenReturn(languageTranslationDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(link).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(languageTranslationDTO)));

        verify(habitFactService, times(1)).getHabitFactOfTheDay(languageId);
    }

    @Test
    void getAll() throws Exception {
        LanguageTranslationDTO langTransDTOMock = Mockito.mock(LanguageTranslationDTO.class);
        PageableDto<LanguageTranslationDTO> expected = Mockito.mock(PageableDto.class);
        expected.setPage(List.of(langTransDTOMock));

        when(habitFactService.getAllHabitFacts(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
            .thenReturn(expected);
        /** below throws : jakarta.servlet.ServletException: Request processing failed: java.lang.IllegalStateException:
         No primary or single unique constructor found for interface org.springframework.data.domain.Pageable **/
        mockMvc.perform(MockMvcRequestBuilders.get(HABIT_FACT_CONTROLLER_LINK).accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void save() throws Exception {
        HabitFactPostDto habitFactPostDto = ModelUtils.getHabitFactPostDto();
        HabitFactVO habitFactVO = ModelUtils.getHabitFactVO();
        HabitFactDtoResponse habitFactDtoResponse = Mockito.mock(HabitFactDtoResponse.class);

        when(habitFactService.save(habitFactPostDto)).thenReturn(habitFactVO);
        when(modelMapper.map(habitFactVO, HabitFactDtoResponse.class)).thenReturn(habitFactDtoResponse);

        /** below throws Request processing failed: jakarta.validation.ValidationException: HV000032: Unable to initialize greencity.validator.LanguageTranslationValidator.
        Because "Cannot invoke "greencity.service.LanguageService.getAllLanguages()" because "<local3>.languageService" is null" **/
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(HABIT_FACT_CONTROLLER_LINK)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(habitFactPostDto))
        );

    }

    @Test
    void update() {
    }

    @Test
    @DisplayName("Should_Delete_Habit_Fact_By_Id")
    void delete() throws Exception {
        Long id = 1L;
        String link = HABIT_FACT_CONTROLLER_LINK.concat("/%d".formatted(id));
        when(habitFactService.delete(id)).thenReturn(id);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete(link));
        response.andExpect(status().isOk());
        verify(habitFactService, times(1)).delete(id);
    }
}