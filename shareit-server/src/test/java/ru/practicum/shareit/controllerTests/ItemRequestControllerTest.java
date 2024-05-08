package ru.practicum.shareit.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequestEndpointTest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequest = invocationOnMock.getArgument(0, ItemRequestDto.class);
                    itemRequest.setId(1);
                    return itemRequest;
                });

        ItemRequestDto itemRequestCreateRequest = ItemRequestDto.builder().description("itemRequest1").build();

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestCreateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("itemRequest1")));
    }

    @Test
    void getOwnItemRequestsEndpointTest() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1)
                .description("хочу вещь 1")
                .created(LocalDateTime.now()).build();
        when(itemRequestService.getAllItemRequestsByOwner(anyLong()))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest))));
    }

    @Test
    void getAllItemRequestsEndpointTest() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1)
                .description("хочу вещь 1")
                .created(LocalDateTime.now()).build();
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests/all?from=0&size=20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest))));
    }

    @Test
    void getItemRequestEndpointTest() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1)
                .description("хочу вещь 1")
                .created(LocalDateTime.now()).build();
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("хочу вещь 1")));
    }
}
