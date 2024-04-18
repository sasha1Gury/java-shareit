package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Comment.model.Comment;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithTime {
    private long id;
    @NotBlank(groups = CreateItemValidation.class)
    private String name;
    @NotBlank(groups = CreateItemValidation.class)
    private String description;
    @NotNull(groups = CreateItemValidation.class)
    private Boolean available;
    private User owner;
    private Long requestId;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private List<Comment> comments;
}
