package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public User add(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    @Transactional
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ? OR friend_id = ?", id, id);
        jdbcTemplate.update("DELETE FROM likes WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public User getById(Long id) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId, boolean confirmed) {
        String sql = "INSERT INTO friendships (user_id, friend_id, confirmed) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, confirmed);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count != null && count > 0) {
            String deleteSql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(deleteSql, userId, friendId);
        }
    }

    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f1 ON u.user_id = f1.friend_id " +
                "JOIN friendships f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        user.setFriends(new HashSet<>(getFriendsIdsByUserId(user.getId())));
        return user;
    }

    private List<Long> getFriendsIdsByUserId(Long userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}