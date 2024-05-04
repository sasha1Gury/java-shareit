package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.CreateUserValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = CreateUserValidation.class)
    private String name;
    @Email(groups = CreateUserValidation.class)
    @NotBlank(groups = CreateUserValidation.class)
    private String email;
}
