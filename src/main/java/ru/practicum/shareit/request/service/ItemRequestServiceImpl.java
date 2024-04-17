package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not exists");
        }
        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }
}
