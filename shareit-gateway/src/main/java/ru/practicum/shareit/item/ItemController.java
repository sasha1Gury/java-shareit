package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.validation.CreateItemValidation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(CreateItemValidation.class) @RequestBody ItemDto itemDto,
                                             @Positive @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Create item {}, owner {}", ownerId, itemDto.toString());
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @PathVariable long itemId,
                                             @Valid @RequestBody ItemDto itemDto,
                                             @Positive @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Update item {}, ownerId {}: ", itemDto, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findItemByUserId(@RequestParam(required = false) Integer from,
                                                    @RequestParam(required = false) Integer size,
                                                    @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get owned items list, ownerId {}, from {}, size {}", userId, from, size);
        return itemClient.findItemByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@Positive @PathVariable long itemId,
                                                @Positive @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get itemId {} by userId {}", itemId, ownerId);
        return itemClient.findItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false) Integer from,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(defaultValue = "") String text) {
        log.info("Search text '{}', from {}, size {}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @Positive @PathVariable long itemId,
                                                @Positive @RequestHeader("X-Sharer-User-Id") long authorId) {
        log.info("Create comment {}, itemId {}, authorId {}: ", commentDto, itemId, authorId);
        return itemClient.createComment(authorId, itemId, commentDto);
    }
}