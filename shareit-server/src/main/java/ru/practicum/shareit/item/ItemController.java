package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Create item by userId={} item={}", userId, itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{userId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto updateItem,
                              @PathVariable("userId") long itemId) {
        return itemService.updateItem(updateItem, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithTime findItemById(@PathVariable("itemId") long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithTime> findItemByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        return itemService.findItemByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String search,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {
        search = search.toLowerCase();
        return itemService.searchItem(search, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestBody CommentDto comment,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createComment(itemId, userId, comment);
    }
}
