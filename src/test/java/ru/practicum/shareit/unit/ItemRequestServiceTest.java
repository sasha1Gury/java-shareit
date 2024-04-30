package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemRequestServiceTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void getItemRequestByIdNormalWay() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new ItemRequest(1, "description",
                        new User(1, "name", "e@mail.ru"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                        )));

        ItemRequestDto itemRequest = itemRequestService.getById(1, 1);

        Assertions.assertEquals(1, itemRequest.getId());
        Assertions.assertEquals("description", itemRequest.getDescription());
        Assertions.assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), itemRequest.getCreated());
    }

    @Test
    void getItemRequestByIdUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(1, 1));

        Assertions.assertEquals("Объект User - 1 не наден", exception.getMessage());
    }

    @Test
    void getItemRequestByIdItemRequestNotFound() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(1, 1));

        Assertions.assertEquals("Объект Item - 1 не наден", exception.getMessage());
    }

    @Test
    void getOwnItemRequestsNormalWay() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        when(mockItemRequestRepository.findAllByOwnerOrderByCreatedDesc(any()))
                .thenReturn(List.of(
                        new ItemRequest(1, "description",
                                new User(1, "name", "e@mail.ru"),
                                LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                        ),

                        new ItemRequest(2, "description",
                                new User(1, "name", "e@mail.ru"),
                                LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                        )
                ));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByOwner(1);

        Assertions.assertEquals(2, itemRequests.size());
        Assertions.assertEquals(1, itemRequests.get(0).getId());
        Assertions.assertEquals(2, itemRequests.get(1).getId());
    }

    @Test
    void createItemRequestNormalWay() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(new ItemRequest(1, "description",
                        new User(1, "name", "e@mail.ru"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                ));

        ItemRequestDto itemRequest = itemRequestService.createItemRequest(ItemRequestDto.builder()
                        .description("описание запроса").build(),
                1);

        assertEquals(1, itemRequest.getId());
        assertEquals("description", itemRequest.getDescription());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), itemRequest.getCreated());
    }

    @Test
    void getAllTest() {
        User ownerRequest = new User(1, "name", "e@mail.ru");
        User owner = new User(2, "name", "another@mail.ru");

        ItemRequest itemRequest = new ItemRequest(1, "description", ownerRequest, LocalDateTime.now());

        when(mockItemRequestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> itemRequestList = itemRequestService.getAllRequests(null, null, 2);

        assertEquals(1, itemRequestList.size());
        assertEquals(1, itemRequestList.get(0).getId());
    }

    @Test
    void getAllTestPage() {
        User ownerRequest = new User(1, "name", "e@mail.ru");
        User owner = new User(2, "name", "another@mail.ru");

        ItemRequest itemRequest = new ItemRequest(1, "description", ownerRequest, LocalDateTime.now());
        Page<ItemRequest> itemRequestPage = new PageImpl<>(List.of(itemRequest));

        when(mockItemRequestRepository.findAll((Pageable) any())).thenReturn(itemRequestPage);
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(owner));

        List<ItemRequestDto> itemRequestList = itemRequestService.getAllRequests(0, 20, 2);

        assertEquals(1, itemRequestList.size());
        assertEquals(1, itemRequestList.get(0).getId());
    }
}
