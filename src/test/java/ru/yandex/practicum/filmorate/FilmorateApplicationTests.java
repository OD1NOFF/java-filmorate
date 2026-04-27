package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private UserController userController;
    private User validUser;

    private FilmController filmController;
    private Film validFilm;

    @Test
	void contextLoads() {
	}

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Test Film");
        validFilm.setDescription("Test Description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

        userController = new UserController();
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testLogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateValidFilm() {
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm.getId());
        assertEquals(validFilm.getName(), createdFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        validFilm.setName("");
        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        validFilm.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldAllowDescriptionExactly200Chars() {
        validFilm.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateTooEarly() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldAllowReleaseDateOnMinDate() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        validFilm.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        validFilm.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void shouldCreateValidUser() {
        User createdUser = userController.createUser(validUser);
        assertNotNull(createdUser.getId());
        assertEquals(validUser.getEmail(), createdUser.getEmail());
    }

    @Test
    void shouldUseLoginWhenNameIsEmpty() {
        validUser.setName("");
        User createdUser = userController.createUser(validUser);
        assertEquals(validUser.getLogin(), createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailWithoutAt() {
        validUser.setEmail("invalidemail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        validUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        validUser.setLogin("login with spaces");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        validUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void shouldAllowBirthdayToday() {
        validUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> userController.createUser(validUser));
    }

}
