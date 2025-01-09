package greencity.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class HabitAssignControllerTest {
    @Mock
    private HabitAssignService habitAssignService;

    @InjectMocks
    private HabitAssignController habitAssignController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .build();
    }

    @Test
    void assignDefaultTest() throws Exception {
        long habitId = 1L;
        HabitAssignManagementDto expectedDto = new HabitAssignManagementDto();
        expectedDto.setId(7L);

        when(habitAssignService.assignDefaultHabitForUser(eq(habitId), any(UserVO.class)))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/habit/assign/" + habitId)
                        .param("habitId", String.valueOf(habitId)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String resultDtoString = result.getResponse().getContentAsString();
                    XmlMapper mapper = new XmlMapper();
                    mapper.registerModule(new JavaTimeModule());
                    HabitAssignManagementDto actualDto = mapper.readValue(resultDtoString, HabitAssignManagementDto.class);
                    Assertions.assertNotNull(actualDto);
                    Assertions.assertEquals(7L, actualDto.getId());
                    Assertions.assertEquals(expectedDto.getId(), actualDto.getId());
                });
        verify(habitAssignService, times(1)).assignDefaultHabitForUser(eq(habitId), any(UserVO.class));
    }

    @Test
    void assignCustomTest() throws Exception {
        long habitId = 1L;
        HabitAssignManagementDto expectedDto = new HabitAssignManagementDto();
        expectedDto.setId(7L);

        when(habitAssignService.assignCustomHabitForUser(eq(habitId), any(UserVO.class), any(HabitAssignCustomPropertiesDto.class)))
                .thenReturn(List.of(expectedDto));

        HabitAssignCustomPropertiesDto requestDto = new HabitAssignCustomPropertiesDto();

        mockMvc.perform(post("/habit/assign/" + habitId + "/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String resultDtoString = result.getResponse().getContentAsString();
                    XmlMapper mapper = new XmlMapper();
                    mapper.registerModule(new JavaTimeModule());
                    List<HabitAssignManagementDto> resultDto = mapper.readValue(resultDtoString, new TypeReference<List<HabitAssignManagementDto>>() {
                    });
                    Assertions.assertNotNull(resultDto);
                    Assertions.assertEquals(1, resultDto.size());
                    Assertions.assertEquals(7L, resultDto.get(0).getId());
                });

        verify(habitAssignService, times(1))
                .assignCustomHabitForUser(eq(habitId), any(UserVO.class), any(HabitAssignCustomPropertiesDto.class));
    }

    @Test
    void updateHabitAssignDurationTest() throws Exception {
        long habitAssignId = 1L;

        HabitAssignUserDurationDto expectedDto = new HabitAssignUserDurationDto();
        expectedDto.setDuration(22);
        expectedDto.setHabitAssignId(habitAssignId);
        expectedDto.setUserId(2L);
        expectedDto.setHabitId(3L);
        expectedDto.setStatus(HabitAssignStatus.REQUESTED);
        expectedDto.setWorkingDays(3);

        when(habitAssignService.updateUserHabitInfoDuration(eq(habitAssignId), any(), any(Integer.class)))
                .thenReturn(expectedDto);

        mockMvc.perform(put("/habit/assign/{habitAssignId}/update-habit-duration",  habitAssignId)
                        .param("duration", String.valueOf(22)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String resultDtoString = result.getResponse().getContentAsString();
                    XmlMapper mapper = new XmlMapper();
                    mapper.registerModule(new JavaTimeModule());
                    HabitAssignUserDurationDto actualDto = mapper.readValue(resultDtoString, HabitAssignUserDurationDto.class);
                    Assertions.assertNotNull(actualDto);
                    Assertions.assertEquals(22, actualDto.getDuration());
                    Assertions.assertEquals(3, actualDto.getWorkingDays());
                    Assertions.assertEquals(habitAssignId, actualDto.getHabitAssignId());
                    Assertions.assertEquals(2L, actualDto.getUserId());
                    Assertions.assertEquals(HabitAssignStatus.REQUESTED, actualDto.getStatus());
                    Assertions.assertEquals(3L, actualDto.getHabitId());
                });

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(eq(habitAssignId), any(), any(Integer.class));
    }
}
