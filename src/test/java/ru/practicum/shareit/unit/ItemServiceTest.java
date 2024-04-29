package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment.model.Comment;
import ru.practicum.shareit.item.Comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.Status.APPROVED;

@SpringBootTest
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserRepository mockUserRepository;
    private UserService userService;

    private Item makeSimpleTestItem() {
        return Item.builder()
                .id(1)
                .owner(new User(1, "user", "e@email.com"))
                .name("name")
                .description("description")
                .available(true).build();
    }

    @Test
    void getByIdNormalWay() {
        Item item = makeSimpleTestItem();
        ItemDtoWithTime itemDtoWithTime = ItemMapper.toDtoWithTime(item);

        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDtoWithTime itemTest = itemService.findItemById(1, 1);

        assertEquals(itemDtoWithTime.getId(), itemTest.getId());
        assertEquals(itemDtoWithTime.getName(), itemTest.getName());
    }

    @Test
    void getByIdItemNotFound() {
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(1, 1));

        Assertions.assertEquals("Объект 1 не наден", exception.getMessage());
    }

    @Test
    void testGetByIdNormalWay() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        User testUserOwner = new User(1, "name", "e@mail.ru");
        User testUserBooker = new User(2, "name", "e@mail.ru");
        Item item = makeSimpleTestItem();

        List<Comment> testCommentList = List.of(
                new Comment(1L, "comment 1 text", item, testUserOwner.getName(), now.minusHours(1)),
                new Comment(2L, "comment 2 text", item, testUserBooker.getName(), now.minusMinutes(1))
        );

        List<Booking> testBookingList = List.of(
                new Booking(1, now.minusHours(2), now.minusHours(1), item, testUserBooker, APPROVED),
                new Booking(2, now.plusHours(1), now.plusHours(2), item, testUserBooker, APPROVED)
        );

        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(mockBookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusOrderByEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(new Booking(1, now.minusHours(2), now.minusHours(1), item, testUserBooker, Status.APPROVED)));
        when(mockBookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(new Booking(2, now.plusHours(1), now.plusHours(2), item, testUserBooker, Status.APPROVED)));
        when(mockBookingRepository.findFirstByItemIdAndStartBeforeAndEndAfterAndStatusOrderByStartDesc(any(), any(), any(), any()))
                        .thenReturn(Optional.of(new Booking(1, now.minusHours(2), now.minusHours(1), item, testUserBooker, Status.APPROVED)));
        when(mockCommentRepository.findAllByItem(any()))
                .thenReturn(testCommentList);

        ItemDtoWithTime itemDtoWithTime = itemService.findItemById(1, 1);

        assertEquals(1, itemDtoWithTime.getId());
        assertEquals(1, itemDtoWithTime.getOwner().getId());
        assertEquals("name", itemDtoWithTime.getName());
        assertEquals("description", itemDtoWithTime.getDescription());
        assertEquals(testBookingList.get(0).getId(), itemDtoWithTime.getLastBooking().getId());
        assertEquals(testBookingList.get(1).getId(), itemDtoWithTime.getNextBooking().getId());
        assertEquals(testCommentList.get(0).getId(), itemDtoWithTime.getComments().get(0).getId());
        assertEquals(testCommentList.get(1).getId(), itemDtoWithTime.getComments().get(1).getId());
    }

    @Test
    void createItemNormalWayWithoutRequest() {
        User owner = new User(1, "name", "e@mail.ru");
        ItemDto itemDto = new ItemDto(1,"name", "description", true, owner, null);

        when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(ItemMapper.toEntity(itemDto));

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1, "name", "e@mail.ru")));

        ItemDto item = itemService.createItem(itemDto, 1);

        assertEquals(itemDto, item);
    }

    @Test
    void createItemNormalWayWithRequest() {
        User owner = new User(1, "name", "e@mail.ru");
        User testRequestUser = new User(2, "name2", "e@mail2.ru");
        ItemRequest testItemRequest = ItemRequest.builder()
                .id(1)
                .owner(testRequestUser)
                .description("хочу вещь name")
                .created(LocalDateTime.now()).build();

        Item item = new Item(1,"name", "description", true, owner, testItemRequest);

        when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        when(mockItemRequestRepository.findById(any()))
                .thenReturn(Optional.of(testItemRequest));

        ItemDto itemDto = itemService.createItem(ItemMapper.toDto(item), 1);

        assertEquals(ItemMapper.toDto(item), itemDto);
    }

    @Test
    void createItemUserNotFound() {
        ItemDto itemCreateRequest = new ItemDto(1,"name", "description", true, null, null);

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(itemCreateRequest, 1));

        Assertions.assertEquals("Объект 1 не наден", exception.getMessage());
    }

    @Test
    void updateItemNormalWay() {
        Item simpleTestItem = makeSimpleTestItem();

        when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(simpleTestItem));
        when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenReturn(simpleTestItem);
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1, "user", "e@email.com")));

        ItemDto item = itemService.updateItem(ItemMapper.toDto(simpleTestItem), 1, 1);

        assertEquals(ItemMapper.toDto(simpleTestItem), item);
    }

    @Test
    void searchItemsPageNormalWay() {
        Item simpleTestItem = makeSimpleTestItem();

        Page<Item> page = new PageImpl<>(List.of(simpleTestItem), PageRequest.of(0, 1), 1);
        when(mockItemRepository.search(any(), anyString())).thenReturn(page);

        List<ItemDto> foundItems = itemService.searchItem("name", 0, 20);

        assertEquals(1, foundItems.size());
    }

    @Test
    void searchItemsNormalWay() {
        Item simpleTestItem = makeSimpleTestItem();

        when(mockItemRepository.search(anyString())).thenReturn(List.of(simpleTestItem));

        List<ItemDto> foundItems = itemService.searchItem("name", null, null);

        assertEquals(1, foundItems.size());
    }

    @Test
    void searchItemsNormalWayEmptyText() {
        List<ItemDto> foundItems = itemService.searchItem("", null, null);

        assertEquals(0, foundItems.size());
    }

}