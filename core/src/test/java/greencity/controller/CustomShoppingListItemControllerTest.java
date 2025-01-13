package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.shoppinglistitem.*;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.*;

import static greencity.ModelUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;


    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    @Mock
    private Validator mockValidator;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
    }


    @Test
    void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        when(customShoppingListItemService.bulkDelete("1,2"))
                .thenReturn(List.of(1L, 2L));

        mockMvc.perform(delete("/custom/shopping-list-items/1/custom-shopping-list-items")
                        .param("ids", "1,2")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).bulkDelete("1,2");
    }

    @Test
    void getAllAvailableCustomShoppingListItemsTest() throws Exception {
        long userId = 1L;
        long habitId = 1L;
        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId))
                .thenReturn(List.of(getCustomShoppingListItemResponseDto()));

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/{habitId}", userId, habitId)
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).findAllAvailableCustomShoppingListItems(userId, habitId);
    }

    @Test
    void getAllCustomShoppingItemsByStatusTest() throws Exception {
        long userId = 1L;

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, "ACTIVE"))
                .thenReturn(List.of(getCustomShoppingListItemResponseDto()));

        mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("status", "ACTIVE")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).findAllUsersCustomShoppingListItemsByStatus(userId, "ACTIVE");
    }

    @Test
    void updateItemStatusTest() throws Exception {
        long userId = 1L;
        long itemId = 2L;
        String itemStatus = "ACTIVE";

        when(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus))
                .thenReturn(getCustomShoppingListItemResponseDto());

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("status", itemStatus)
                        .param("itemId", String.valueOf(itemId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).updateItemStatus(userId, itemId, itemStatus);
    }

    @Test
    void updateItemStatusToDoneTest() throws Exception {
        long userId = 1L;
        long itemId = 2L;

        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done", userId)
                        .param("itemId", String.valueOf(itemId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).updateItemStatusToDone(userId, itemId);
    }

    @Test
    void saveUserCustomShoppingListItemsTest() throws Exception {
        long userId = 1L;
        long habitAssignId = 1L;

        CustomShoppingListItemSaveRequestDto requestDto = new CustomShoppingListItemSaveRequestDto();
        requestDto.setText("text");
        BulkSaveCustomShoppingListItemDto bulkSaveCustomShoppingListItemDto = new BulkSaveCustomShoppingListItemDto(List.of(requestDto));

        when(customShoppingListItemService.save(bulkSaveCustomShoppingListItemDto, userId, habitAssignId))
                .thenReturn(List.of(getCustomShoppingListItemResponseDto()));

        String jsonContent = new ObjectMapper().writeValueAsString(bulkSaveCustomShoppingListItemDto);


        mockMvc.perform(post("/custom/shopping-list-items/{userId}/{habitAssignId}/custom-shopping-list-items", userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(customShoppingListItemService, times(1)).save(bulkSaveCustomShoppingListItemDto, userId, habitAssignId);

    }
}
