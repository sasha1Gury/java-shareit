package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.validation.CreateItemValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    @PositiveOrZero
    private long id;
    @NotBlank(groups = CreateItemValidation.class)
    private String name;
    @NotBlank(groups = CreateItemValidation.class)
    private String description;
    @NotNull(groups = CreateItemValidation.class)
    private Boolean available;
    //private User owner;
    private Long requestId;
}
