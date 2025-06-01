package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.*;
import java.sql.Date;
import java.util.*;

@RequiredArgsConstructor
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Override
    @Transactional
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateGenres(film);
        return film;
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateGenres(film);
        return getById(film.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Transactional
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Transactional
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
        film.setLikes(new HashSet<>(getLikesByFilmId(film.getId())));

        return film;
    }

    private List<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM film_genre fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    private List<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();

            for (Genre genre : film.getGenres()) {
                batchArgs.add(new Object[]{film.getId(), genre.getId()});
            }

            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
}