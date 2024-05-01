package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getAllItemRequestsByOwner(long userId);

    List<ItemRequestDto> getAllRequests(Integer from, Integer size, long userId);

    ItemRequestDto getById(long requestId, long userId);
}
