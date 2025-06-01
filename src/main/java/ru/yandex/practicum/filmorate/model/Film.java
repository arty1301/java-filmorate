package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */


@Data
public class Film {
    private Long id;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не должна быть больше 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть указана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}