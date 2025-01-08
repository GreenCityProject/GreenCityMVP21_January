package greencity.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController).build();
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
}
