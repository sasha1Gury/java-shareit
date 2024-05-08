package ru.practicum.shareit.request.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOwnerOrderByCreatedDesc(User owner);

    Optional<ItemRequest> findById(long id);

    @NonNull
    Page<ItemRequest> findAll(@NonNull Pageable pageable);

    @NonNull
    List<ItemRequest> findAll();
}
