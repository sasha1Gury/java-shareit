package ru.practicum.shareit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "UserServiceImpl")
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUserEndpointTest() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1);
                    return userDto;
                });
        UserDto testUser = new UserDto(0, "name", "e@email.ru");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.email", is("e@email.ru")));
    }

    @Test
    void updateUserEndpointTest() throws Exception {

        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, UserDto.class));

        UserDto testUserDto = new UserDto(1, "name1", "e1@mail.ru");

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(testUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.email", is("e1@mail.ru")));
    }

    @Test
    void getAllUsersEndpointTest() throws Exception {
        UserDto testUser = new UserDto(0, "name1", "e1@mail.ru");

        when(userService.getAllUsers())
                .thenReturn(List.of(testUser));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(testUser))));
    }

    @Test
    void getUserEndpointTest() throws Exception {
        UserDto testUser = new UserDto(0, "name1", "e1@mail.ru");

        when(userService.findUserById(1))
                .thenReturn(testUser);

        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(testUser)));
    }

    @Test
    void deleteUserEndpointTest() throws Exception {
        UserDto testUser = new UserDto(0, "name1", "e1@mail.ru");

        mockMvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}