package ru.practicum.shareit.repositoryIntegration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Comment.model.Comment;
import ru.practicum.shareit.item.Comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private Comment comment1;

    @BeforeEach
    void beforeEach() {
        User ownerUser = em.persistAndFlush(User.builder().name("name").email("e@mail.ru").build());
        item = em.persistAndFlush(Item.builder().name("дрель").description("хорошая быстрая дрель")
                .available(true).owner(ownerUser).build());
        comment1 = em.persistAndFlush(Comment.builder().item(item).authorName(ownerUser.getName()).text("комментарий к дрели")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusMinutes(20)).build());
    }

    @Test
    void findAllByItem() {
        List<Comment> commentList = commentRepository.findAllByItem(item);

        assertEquals(1, commentList.size());
        assertEquals(comment1.getId(), commentList.get(0).getId());
    }
}
