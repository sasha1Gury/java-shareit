package ru.practicum.shareit.repositoryIntegration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestAuthor;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        requestAuthor = em.persistAndFlush(User.builder().name("name").email("e@mail.ru").build());
        itemRequest1 = em.persistAndFlush(ItemRequest.builder().description("хочу хорошую вещь")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(25))
                .owner(requestAuthor).build());
        itemRequest2 = em.persistAndFlush(ItemRequest.builder().description("хочу другую хорошую вещь")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(20))
                .owner(requestAuthor).build());
    }

    @Test
    void findByRequestAuthor_idOrderByCreatedDescTest() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByOwnerOrderByCreatedDesc(requestAuthor);

        assertEquals(2, itemRequestList.size());
        assertEquals(itemRequest2.getId(), itemRequestList.get(0).getId());
        assertEquals(itemRequest1.getId(), itemRequestList.get(1).getId());
    }

    @Test
    void findByRequestAuthor_idNotOrderByCreatedDescTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);

        assertEquals(2, itemRequestPage.getContent().size());
    }
}
