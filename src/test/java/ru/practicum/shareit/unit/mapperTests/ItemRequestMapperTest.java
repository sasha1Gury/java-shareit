package ru.practicum.shareit.unit.mapperTests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    @Test
    public void testToEntity() {
        ItemRequestDto dto = ItemRequestDto.builder().build();
        dto.setId(1L);
        dto.setDescription("Test description DTO");

        ItemRequest itemRequest = ItemRequestMapper.toEntity(dto);

        assertNotNull(itemRequest);
        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void testToDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");

        ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);

        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
    }
}
