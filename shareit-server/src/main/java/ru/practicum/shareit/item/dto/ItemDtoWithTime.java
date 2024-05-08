package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Comment.model.Comment;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoWithTime {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private List<Comment> comments;
}
