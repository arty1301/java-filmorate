package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User add(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id = {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} обновлен", user);
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь с id = {} удален", id);
    }
}