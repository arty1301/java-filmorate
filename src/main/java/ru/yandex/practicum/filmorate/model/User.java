package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Set<Long> friends = new HashSet<>();


    @NotBlank(message = "Емейл не может быть пустым")
    @Email(message = "Емейл должен содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не можут быть в будущем")
    private LocalDate birthday;
}
