package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.user.validation.CreateUserValidation;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank(groups = CreateUserValidation.class)
    private String name;
    @Email(groups = CreateUserValidation.class)
    @NotBlank(groups = CreateUserValidation.class)
    @Column(unique = true)
    private String email;
}
