package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        Mpa mpa = mpaStorage.getMpaById(1L);
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);
    }

    @Test
    void shouldAddAndGetFilm() {
        Film addedFilm = filmStorage.add(testFilm);
        Film retrievedFilm = filmStorage.getById(addedFilm.getId());

        assertThat(retrievedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(testFilm);

        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.getMpa().getName()).isEqualTo("G");
    }

    @Test
    void shouldUpdateFilm() {
        Film addedFilm = filmStorage.add(testFilm);
        addedFilm.setName("Updated Name");

        Film updatedFilm = filmStorage.update(addedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getMpa().getName()).isEqualTo("G");
    }

    @Test
    void shouldGetAllFilms() {
        filmStorage.add(testFilm);
        List<Film> films = filmStorage.getAll();

        assertThat(films)
                .hasSize(1)
                .extracting(Film::getName)
                .containsExactly("Test Film");
    }
}