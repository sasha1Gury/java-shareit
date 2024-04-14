package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_request")
public class ItemRequest {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
}
