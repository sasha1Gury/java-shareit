package ru.practicum.shareit.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemServiceMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateItem() throws Exception {
        // given
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        when(itemServiceMock.createItem(itemDto, userId)).thenReturn(itemDto);

        // when
        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        ItemDto response = objectMapper.readValue(result.getModelAndView().getModel(), ItemDto.class);
        assertEquals(itemDto, response);
    }

    @Test
    public void testUpdateItem() throws Exception {
        // given
        long userId = 1L;
        long itemId = 1L;
        ItemDto updateItem = new ItemDto("Item 1 updated", "Description 1 updated", true);
        when(itemServiceMock.updateItem(updateItem, itemId, userId)).thenReturn(updateItem);

        // when
        MvcResult result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        ItemDto response = objectMapper.readValue(result.getModelAndView().getModel(), ItemDto.class);
        assertEquals(updateItem, response);
    }

    @Test
    public void testFindItemById() throws Exception {
        // given
        long userId = 1L;
        long itemId = 1L;
        ItemDtoWithTime itemDtoWithTime = new ItemDtoWithTime("Item 1", "Description 1", true, Instant.now());
        when(itemServiceMock.findItemById(itemId, userId)).thenReturn(itemDtoWithTime);

        // when
        MvcResult result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        ItemDtoWithTime response = objectMapper.readValue(result.getModelAndView().getModel(), ItemDtoWithTime.class);
        assertEquals(itemDtoWithTime, response);
    }

    @Test
    public void testFindItemByUserId() throws Exception {
        // given
        long userId = 1L;
        List<ItemDtoWithTime> items = Arrays.asList(new ItemDtoWithTime("Item 1", "Description 1", true, Instant.now()));
        when(itemServiceMock.findItemByUserId(userId, null, null)).thenReturn(items);

        // when
        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<ItemDtoWithTime> response = objectMapper.readValue(result.getModelAndView().getModel(), new TypeReference<List<ItemDtoWithTime>>() {});
        assertEquals(items, response);
    }

    @Test
    public void testSearchItems() throws Exception {
        // given
        String search = "item";
        List<ItemDto> items = Arrays.asList(new ItemDto("Item 1", "Description 1", true));
        when(itemServiceMock.searchItem(search, null, null)).thenReturn(items);

        // when
        MvcResult result = mockMvc.perform(get("/items/search")
                        .param("text", search))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<ItemDto> response = objectMapper.readValue(result.getModelAndView().getModel(), new TypeReference<List<ItemDto>>() {});
        assertEquals(items, response);
    }

    @Test
    public void testCreateComment() throws Exception {
        // given
        long userId = 1L;
        long itemId = 1L;
        CommentDto comment = new CommentDto("Comment 1");
        when(itemServiceMock.createComment(itemId, userId, comment)).thenReturn(comment);

        // when
        MvcResult result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        CommentDto response = objectMapper.readValue(result.getModelAndView().getModel(), CommentDto.class);
        assertEquals(comment, response);
    }
}