package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import java.time.LocalDateTime;
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
    long id = 2;

    @Test
    public void findItemById() {
        //MockitoAnnotations.initMocks(this);
        LocalDateTime currentTime = LocalDateTime.of(2024, 4, 21, 12, 0);
        User mockUser = new User(); // Set up a mock user
        mockUser.setId(id);
        mockUser.setName("testName");
        mockUser.setEmail("testEmail");
        ItemDto itemDto = new ItemDto(
                23,                  // id
                "Sample Name",      // name
                "Sample Description", // description
                true,               // available
                mockUser,         // owner
                null                // requestId
        );
        Item item = ItemMapper.toEntity(itemDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem(item)).thenReturn(new ArrayList<>());

        ItemDtoWithTime result = itemService.findItemById(item.getId(), mockUser.getId());
        // Assertions
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertTrue(result.getComments().isEmpty());

        // Verify interactions
        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, times(1)).findAllByItem(item);
    }
}
