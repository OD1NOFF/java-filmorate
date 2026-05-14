package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.db.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    // ==================== USER TESTS ====================

    @Test
    void testGetAllUsers() {
        List<User> users = userStorage.getAll();
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetUserById() {
        Optional<User> user = userStorage.getById(1);
        assertThat(user)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(1);
                    assertThat(u.getEmail()).isEqualTo("1@mail.com");
                });
    }

    @Test
    void testGetUserByIdNotFound() {
        Optional<User> user = userStorage.getById(999);
        assertThat(user).isEmpty();
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("new@mail.com");
        user.setLogin("newLogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userStorage.add(user);
        assertThat(saved.getId()).isPositive();

        Optional<User> found = userStorage.getById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(u -> assertThat(u.getEmail()).isEqualTo("new@mail.com"));
    }

    @Test
    void testUpdateUser() {
        User user = userStorage.getById(1).orElseThrow();
        user.setName("Updated Name");
        userStorage.update(user);

        Optional<User> updated = userStorage.getById(1);
        assertThat(updated).isPresent()
                .hasValueSatisfying(u -> assertThat(u.getName()).isEqualTo("Updated Name"));
    }

    @Test
    void testAddFriend() {
        userStorage.addFriend(1, 4);
        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).extracting(User::getId).contains(4);
    }

    @Test
    void testRemoveFriend() {
        userStorage.removeFriend(1, 2);
        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).extracting(User::getId).doesNotContain(2);
    }

    @Test
    void testGetFriends() {
        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).isNotEmpty();
        assertThat(friends).extracting(User::getId).contains(2, 3);
    }

    @Test
    void testGetCommonFriends() {
        List<User> common = userStorage.getCommonFriends(1, 2);
        assertThat(common).isNotEmpty();
        assertThat(common).extracting(User::getId).contains(3);
    }
}