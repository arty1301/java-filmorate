package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("28.12.1895", formatter);

    @Transactional
    public Film addFilm(Film film) {
        validateFilm(film);

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.getMpaById(film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreStorage.getGenreById(genre.getId()));
        }

        return filmStorage.add(film);
    }

    @Transactional
    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    @Transactional
    public void deleteFilm(Long id) {
        filmStorage.delete(id);
    }

    @Transactional
    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    @Transactional
    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        return genreStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Long id) {
        return mpaStorage.getMpaById(id);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть указана");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }
}