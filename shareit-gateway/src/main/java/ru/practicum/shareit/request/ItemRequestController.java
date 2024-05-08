package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create item request{}, owner {}", itemRequestDto, userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get own item requests authorId {}", userId);
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size,
                                                 @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all item requests from {} size {} requestAuthorId {}", from, size, userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@Positive @PathVariable long requestId,
                                          @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requestId {} for userId {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}