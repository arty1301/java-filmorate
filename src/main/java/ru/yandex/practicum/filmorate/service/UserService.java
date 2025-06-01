package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    @Transactional
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userStorage.getById(user.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        userStorage.delete(id);
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }

        requireUserExists(userId);
        requireUserExists(friendId);

        userStorage.addFriend(userId, friendId, false);
    }

    private void requireUserExists(Long userId) {
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}