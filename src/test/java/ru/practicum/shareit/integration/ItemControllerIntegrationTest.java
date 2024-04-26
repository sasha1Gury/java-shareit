package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "ItemServiceImpl")
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(ItemDto.class), eq(1L)))
                .thenReturn(new ItemDto(1,"itemName", "description", true,
                        new User(1, "name", "e@email.ru"), null));

        ItemDto itemCreateRequest = new ItemDto(1,"itemName", "description", true,
                new User(1, "name", "e@email.ru"), null);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ItemDto.class));

        ItemDto itemDto = ItemDto.builder().id(1).name("itemName2").description("description").build();

        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName2")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    void getOwnedItemsListEndpointTest() throws Exception {
        User owner = new User(1, "user", "e@e.com");
        ItemDtoWithTime item1 = ItemDtoWithTime.builder().id(1).owner(owner)
                .name("itemName1").description("description").available(true).build();
        ItemDtoWithTime item2 = ItemDtoWithTime.builder().id(2).owner(owner)
                .name("itemName2").description("description").available(true).build();

        when(itemService.findItemByUserId(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item1, item2))));
    }

    @Test
    void getOwnedItemsListEndpointExceptionTest() throws Exception {
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemEndpointTest() throws Exception {
        ItemDtoWithTime item1 = ItemDtoWithTime.builder().id(1).owner(new User(1, "user", "e@e.com"))
                .name("вещь1").description("описание 1").available(true).build();

        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(item1);

        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item1)));
    }

}