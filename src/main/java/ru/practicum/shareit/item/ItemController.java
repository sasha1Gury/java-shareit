package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.CreateItemValidation;

import javax.validation.Valid;
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
                              @RequestBody @Validated(CreateItemValidation.class) ItemDto itemDto) {
        log.info("Create item by userId={} item={}", userId, itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{userId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto updateItem,
                              @PathVariable("userId") long itemId) {
        return itemService.updateItem(updateItem, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithTime findItemById(@PathVariable("itemId") long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithTime> findItemByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String search) {
        search = search.toLowerCase();
        return itemService.searchItem(search);
    }
}
