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
        // Создание фиктивного объекта Comment
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        // Укажите другие поля, если это необходимо

        // Вызов метода toDto
        CommentDto commentDto = CommentMapper.toDto(comment);

        // Проверка результатов
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        // Проверьте другие поля, если это необходимо
    }

    @Test
    public void testToEntity() {
        // Создание фиктивного объекта CommentDto
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment DTO");
        // Укажите другие поля, если это необходимо

        // Вызов метода toEntity
        Comment comment = CommentMapper.toEntity(commentDto);

        // Проверка результатов
        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        // Проверьте другие поля, если это необходимо
    }
}
