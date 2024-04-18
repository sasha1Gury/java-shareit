package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = CreateItemValidation.class)
    private String name;
    @NotBlank(groups = CreateItemValidation.class)
    private String description;
    @NotNull(groups = CreateItemValidation.class)
    private Boolean available;
    private User owner;
    private Long requestId;
}
