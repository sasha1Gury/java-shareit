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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User ownerUser;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        ownerUser = em.persistAndFlush(User.builder().name("name").email("e@mail.ru").build());
        item1 = em.persistAndFlush(Item.builder().name("дрель").description("хорошая быстрая дрель")
                .available(true).owner(ownerUser).build());
        item2 = em.persistAndFlush(Item.builder().name("компрессор").description("мощный компрессор")
                .available(true).owner(ownerUser).build());
    }

    @Test
    void findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTest() {
        String text = "компрессор";
        Pageable page = PageRequest.of(0, 20);
        Page<Item> itemPage = itemRepository
                .search(page, text);

        List<Item> itemList = itemPage.toList();
        assertEquals(1, itemList.size());
        assertEquals(item2.getId(), itemList.get(0).getId());
    }

    @Test
    void findByOwnerIdOrderByIdTest() {
        Pageable page = PageRequest.of(0, 20);
        Page<Item> itemPage = itemRepository.findAllByOwner(page, ownerUser);

        List<Item> itemList = itemPage.toList();
        assertEquals(2, itemList.size());
        assertEquals(item1.getId(), itemList.get(0).getId());
        assertEquals(item2.getId(), itemList.get(1).getId());
    }
}
