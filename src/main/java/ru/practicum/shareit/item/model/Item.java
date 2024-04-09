package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.validation.CreateItemValidation;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank(groups = CreateItemValidation.class)
    private String name;
    @NotBlank(groups = CreateItemValidation.class)
    private String description;
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
    @OneToOne
    @JoinColumn(name = "request")
    private ItemRequest request;
}
