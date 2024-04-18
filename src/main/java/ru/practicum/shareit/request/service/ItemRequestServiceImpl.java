package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User - " + userId));
        itemRequest.setOwner(owner);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByOwner(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User - " + userId));
        return itemRequestRepository.findAllByOwnerOrderByCreatedDesc(user).stream()
                .map(ItemRequestMapper::toDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository
                        .findAllByRequest_Id(itemRequestDto.getId()).stream()
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer from, Integer size, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User - " + userId));
        if (from == null || size == null) {
            return itemRequestRepository.findAll().stream()
                    .filter(itemRequest -> !itemRequest.getOwner().equals(owner))
                    .map(ItemRequestMapper::toDto)
                    .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository
                            .findAllByRequest_Id(itemRequestDto.getId()).stream()
                            .map(ItemMapper::toDto)
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        Pageable page = PageRequest.of(from, size);
        return itemRequestRepository.findAll(page).stream()
                .filter(itemRequest -> !itemRequest.getOwner().equals(owner))
                .map(ItemRequestMapper::toDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository
                        .findAllByRequest_Id(itemRequestDto.getId()).stream()
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(long requestId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User - " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item - " + requestId));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        itemRequestDto.setItems(itemRepository
                .findAllByRequest_Id(itemRequestDto.getId()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }
}
