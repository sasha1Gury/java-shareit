package ru.practicum.shareit.item.Comment.model;

import ru.practicum.shareit.item.Comment.dto.CommentDto;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthorName(),
                comment.getCreated());
    }

    public static Comment toEntity(CommentDto dto) {
        return new Comment(dto.getId(),
                dto.getText(),
                dto.getItem(),
                dto.getAuthorName(),
                dto.getCreated());
    }
}
