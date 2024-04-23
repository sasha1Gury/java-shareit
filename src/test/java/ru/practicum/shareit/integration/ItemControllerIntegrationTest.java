package ru.practicum.shareit.integration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    @Qualifier("ItemServiceImpl") private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    //@MockBean
    //@Qualifier("ItemServiceImpl") private ItemService itemServiceMock;
    private User user1;

    @BeforeEach
    public void setup() {
        user1 = new User(1, "User 1", "email1@gmail.com");
        userRepository.save(user1);
    }

    @Test
    public void testCreateItem() throws Exception {
        long userId = 1;
        ItemDto itemDto = new ItemDto(1, "itemName", "itemDescription", true, user1, null);
        //when(itemServiceMock.createItem(itemDto, userId)).thenReturn(itemDto);

        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ItemDto response = objectMapper.readValue(responseBody, ItemDto.class);
        assertEquals(itemDto, response);
    }

    /*@Test
    public void testUpdateItem() throws Exception {
        // given
        long userId = 1;
        long itemId = 1;
        ItemDto updateItem = new ItemDto(1, "newName", "newDescription", true, user1, null);
        ItemDto expectedItem = new ItemDto(1, "newName", "newDescription", true, user1, null);
         itemRepository.save(ItemMapper.toEntity(updateItem));
        //when(itemService.updateItem(updateItem, itemId, userId)).thenReturn(expectedItem);

        // when
        MvcResult result = mockMvc.perform(patch("/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        ItemDto response = objectMapper.readValue(responseBody, ItemDto.class);
        assertEquals(expectedItem, response);
    }*/


}