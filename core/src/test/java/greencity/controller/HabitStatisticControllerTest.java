package greencity.controller;

import greencity.dto.habitstatistic.*;
import greencity.dto.user.UserVO;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {

    @Mock
    private HabitStatisticService habitStatisticService;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    @Test
    void findAllByHabitIdTest() {
        Long habitId = 1L;
        GetHabitStatisticDto mockResponse = new GetHabitStatisticDto();
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(mockResponse);

        ResponseEntity<GetHabitStatisticDto> response = habitStatisticController.findAllByHabitId(habitId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void findAllByHabitIdNotFoundTest() {
        Long habitId = 999L;
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenThrow(new RuntimeException("Habit not found"));

        try {
            habitStatisticController.findAllByHabitId(habitId);
        } catch (RuntimeException e) {
            assertEquals("Habit not found", e.getMessage());
        }
    }

    @Test
    void findAllStatsByHabitAssignIdTest() {
        Long habitAssignId = 1L;
        List<HabitStatisticDto> mockResponse = Collections.singletonList(new HabitStatisticDto());
        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId)).thenReturn(mockResponse);

        ResponseEntity<List<HabitStatisticDto>> response = habitStatisticController.findAllStatsByHabitAssignId(habitAssignId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void findAllStatsByHabitAssignIdNotFoundTest() {
        Long habitAssignId = 999L;
        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId)).thenThrow(new RuntimeException("Habit assign not found"));

        try {
            habitStatisticController.findAllStatsByHabitAssignId(habitAssignId);
        } catch (RuntimeException e) {
            assertEquals("Habit assign not found", e.getMessage());
        }
    }

    @Test
    void saveTest() {
        Long habitId = 1L;
        AddHabitStatisticDto inputDto = new AddHabitStatisticDto();
        HabitStatisticDto mockResponse = new HabitStatisticDto();
        UserVO userVO = UserVO.builder().id(1L).build();
        when(habitStatisticService.saveByHabitIdAndUserId(habitId, 1L, inputDto)).thenReturn(mockResponse);

        ResponseEntity<HabitStatisticDto> response = habitStatisticController.saveHabitStatistic(inputDto, userVO, habitId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void saveBadRequestTest() {
        Long habitId = 1L;
        AddHabitStatisticDto inputDto = new AddHabitStatisticDto();
        UserVO userVO = UserVO.builder().id(1L).build();
        when(habitStatisticService.saveByHabitIdAndUserId(habitId, 1L, inputDto)).thenThrow(new RuntimeException("Invalid input"));

        try {
            habitStatisticController.saveHabitStatistic(inputDto, userVO, habitId);
        } catch (RuntimeException e) {
            assertEquals("Invalid input", e.getMessage());
        }
    }

    @Test
    void updateTest() {
        Long id = 1L;
        UpdateHabitStatisticDto inputDto = new UpdateHabitStatisticDto();
        UpdateHabitStatisticDto mockResponse = new UpdateHabitStatisticDto();
        UserVO userVO = UserVO.builder().id(1L).build();
        when(habitStatisticService.update(id, 1L, inputDto)).thenReturn(mockResponse);

        ResponseEntity<UpdateHabitStatisticDto> response = habitStatisticController.updateStatistic(id, userVO, inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void updateNotFoundTest() {
        Long id = 999L;
        UpdateHabitStatisticDto inputDto = new UpdateHabitStatisticDto();
        UserVO userVO = UserVO.builder().id(1L).build();
        when(habitStatisticService.update(id, 1L, inputDto)).thenThrow(new RuntimeException("Statistic not found"));

        try {
            habitStatisticController.updateStatistic(id, userVO, inputDto);
        } catch (RuntimeException e) {
            assertEquals("Statistic not found", e.getMessage());
        }
    }

    @Test
    void getTodayStatsTest() {
        String language = "en";
        List<HabitItemsAmountStatisticDto> mockResponse = Collections.singletonList(new HabitItemsAmountStatisticDto());
        Locale locale = new Locale(language);
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(language)).thenReturn(mockResponse);

        ResponseEntity<List<HabitItemsAmountStatisticDto>> response = habitStatisticController.getTodayStatisticsForAllHabitItems(locale);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getAcquiredCountTest() {
        Long userId = 1L;
        Long mockResponse = 10L;
        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(mockResponse);

        ResponseEntity<Long> response = habitStatisticController.findAmountOfAcquiredHabits(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getInProgressCountTest() {
        Long userId = 1L;
        Long mockResponse = 5L;
        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(mockResponse);

        ResponseEntity<Long> response = habitStatisticController.findAmountOfHabitsInProgress(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }
}
