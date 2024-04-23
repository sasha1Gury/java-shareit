package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private User user1;

    @BeforeEach
    public void setup() {
        user1 = new User(1, "User 1", "email1@gmail.com");
        userRepository.save(user1);
    }

    @Test
    public void testCreateItem() throws Exception {
        long userId = 1;
        ItemDto itemDto = new ItemDto(
                1,
                "itemName",
                "itemDescription",
                true,
                user1,
                null
        );

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

    @Test
    public void testUpdateItem() throws Exception {
        // given
        long userId = 1;
        ItemDto itemDto = new ItemDto(
                1,
                "itemName",
                "itemDescription",
                true,
                user1,
                null
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn();

        ItemDto updateItem = new ItemDto(1, "newName",
                "newDescription", true, user1, null);

        // when
        MvcResult result = mockMvc.perform(patch("/items/" + 1)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        ItemDto response = objectMapper.readValue(responseBody, ItemDto.class);
        assertEquals(updateItem, response);
    }

    @Test
    public void getAllUsersTest() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/items")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        assertNotNull(responseBody);
    }

    @Test
    public void findItemByIdTest() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON));

        String responseBody = result.andReturn().getResponse().getContentAsString();
        ItemDto itemDto = objectMapper.readValue(responseBody, ItemDto.class);

        assertEquals(itemDto.getId(), 1);
    }

    @Test
    public void searchTest() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                .param("text", "itemName")
                .accept(MediaType.APPLICATION_JSON));
        String responseBody = result.andReturn().getResponse().getContentAsString();
        ItemDto itemDto = objectMapper.readValue(responseBody, ItemDto.class);

        System.out.println(itemDto.toString());
        assertEquals(itemDto.getName(), "itemName");
    }


}