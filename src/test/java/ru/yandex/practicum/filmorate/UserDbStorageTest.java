package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;


import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("testLogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldAddAndGetUser() {
        User addedUser = userStorage.add(testUser);
        User retrievedUser = userStorage.getById(addedUser.getId());

        assertThat(retrievedUser)
                .isNotNull()
                .isEqualTo(addedUser)
                .hasFieldOrPropertyWithValue("login", "testLogin");
    }

    @Test
    void shouldUpdateUser() {
        User addedUser = userStorage.add(testUser);
        addedUser.setName("Updated Name");

        User updatedUser = userStorage.update(addedUser);

        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("name", "Updated Name");
    }
}