package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);

    void addFriend(Long userId, Long friendId, boolean confirmed);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    boolean userExists(Long userId);
}
