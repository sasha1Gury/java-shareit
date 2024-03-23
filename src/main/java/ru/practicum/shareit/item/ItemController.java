package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item createItem(@RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody Item item) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{userId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody Map<String, Object> updates,
                           @PathVariable("userId") long itemId) {
        return itemService.updateItem(updates, itemId, userId);
    }
}
