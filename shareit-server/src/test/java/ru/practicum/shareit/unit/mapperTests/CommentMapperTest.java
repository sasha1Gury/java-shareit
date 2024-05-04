package ru.practicum.shareit.unit.mapperTests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment.dto.CommentDto;
import ru.practicum.shareit.item.Comment.model.Comment;
import ru.practicum.shareit.item.Comment.model.CommentMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {
    @Test
    public void testToDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        CommentDto commentDto = CommentMapper.toDto(comment);

        // Проверка результатов
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    public void testToEntity() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment DTO");

        Comment comment = CommentMapper.toEntity(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
    }
}
