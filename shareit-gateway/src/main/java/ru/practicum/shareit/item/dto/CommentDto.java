package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class CommentDto {
    private long id;

    @NotEmpty
    private String text;

    private String authorName;
    private LocalDateTime created;
}