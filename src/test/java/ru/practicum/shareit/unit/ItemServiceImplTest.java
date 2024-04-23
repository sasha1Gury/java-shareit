package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private long itemId = 2;
    private long userId = 1;

    @Test
    public void findItemById() {

        // Set up a mock user
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("testName");
        mockUser.setEmail("testEmail");

        // Set up a mock item
        ItemDto itemDto = new ItemDto(
                itemId,
                "Sample Name",
                "Sample Description",
                true,
                mockUser,
                null
        );
        Item item = ItemMapper.toEntity(itemDto);

        // Set up mock repository responses
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem(item)).thenReturn(new ArrayList<>());

        // Call the method under test
        ItemDtoWithTime result = itemService.findItemById(itemId, userId);

        // Assertions
        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertTrue(result.getComments().isEmpty());

        // Verify interactions
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findAllByItem(item);
    }
}