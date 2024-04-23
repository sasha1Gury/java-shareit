package ru.practicum.shareit.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("testEmail@email.com");
        user.setName("name");

        UserDto userDto = UserMapper.toDto(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("testEmail@email.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Create a user first
        User user = new User();
        user.setId(1);
        user.setEmail("originalEmail@email.com");
        user.setName("originalName");

        UserDto userDto = UserMapper.toDto(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        // Update the user's name and email
        UserDto updates = new UserDto(1, "newName", "newEmail@email.com");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.email").value("newEmail@email.com"));
    }

    @Test
    public void findUserById() throws Exception {
        // Create a test user
        User user = new User();
        user.setId(1);
        user.setEmail("johndoe@example.com");
        user.setName("John Doe");

        UserDto userDto = UserMapper.toDto(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        // Send a GET request to /users/{userId}
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("John Doe")))
                .andExpect(jsonPath("$.email", equalTo("johndoe@example.com")));
    }

    @Test
    public void getAllUsers() throws Exception {
        // Create test users
        UserDto user1 = new UserDto(2L, "John Doe", "Johndoe@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        // Send a GET request to /users
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON));

        // Verify the response
        String responseBody = result.andReturn().getResponse().getContentAsString();
        List<UserDto> userDtos = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals(2, userDtos.size());
        assertEquals("John Doe", userDtos.get(1).getName());
        assertEquals("Johndoe@example.com", userDtos.get(1).getEmail());
    }

    @Test
    public void deleteUserTest() throws Exception {
        // Create test user
        UserDto user = new UserDto(5L, "John Doe", "John2doe@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Send a DELETE request to /users/{userId}
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/users/5")
                .accept(MediaType.APPLICATION_JSON));

        // Verify the response
        result.andExpect(status().is5xxServerError());

        // Verify that the user was deleted
        ResultActions result2 = mockMvc.perform(MockMvcRequestBuilders.get("/users/5")
                .accept(MediaType.APPLICATION_JSON));
        String responseBody = result2.andReturn().getResponse().getContentAsString();
        UserDto userDto = objectMapper.readValue(responseBody, UserDto.class);
        assertEquals(new UserDto(0, null, null), userDto);
    }

}