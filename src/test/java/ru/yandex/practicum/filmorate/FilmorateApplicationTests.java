package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private UserController userController;
    private User validUser;

    private FilmController filmController;
    private Film validFilm;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
        filmController = new FilmController(filmService);
        validFilm = new Film();
        validFilm.setName("Test Film");
        validFilm.setDescription("Test Description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

        UserService userService = new UserService(new InMemoryUserStorage());
        userController = new UserController(userService);
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testLogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    private <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateValidFilm() {
        assertDoesNotThrow(() -> validate(validFilm));
        Film createdFilm = filmController.createFilm(validFilm);
        assertNotNull(createdFilm.getId());
        assertEquals(validFilm.getName(), createdFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        validFilm.setName("");
        assertThrows(ConstraintViolationException.class, () -> validate(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        validFilm.setDescription("a".repeat(201));
        assertThrows(ConstraintViolationException.class, () -> validate(validFilm));
    }

    @Test
    void shouldAllowDescriptionExactly200Chars() {
        validFilm.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> validate(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateTooEarly() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConstraintViolationException.class, () -> validate(validFilm));
    }

    @Test
    void shouldAllowReleaseDateOnMinDate() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> validate(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        validFilm.setDuration(0);
        assertThrows(ConstraintViolationException.class, () -> validate(validFilm));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        validFilm.setDuration(-1);
        assertThrows(ConstraintViolationException.class, () -> validate(validFilm));
    }

    @Test
    void shouldCreateValidUser() {
        assertDoesNotThrow(() -> validate(validUser));
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
        assertThrows(ConstraintViolationException.class, () -> validate(validUser));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        validUser.setEmail("");
        assertThrows(ConstraintViolationException.class, () -> validate(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        validUser.setLogin("login with spaces");
        assertThrows(ConstraintViolationException.class, () -> validate(validUser));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        validUser.setLogin("");
        assertThrows(ConstraintViolationException.class, () -> validate(validUser));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ConstraintViolationException.class, () -> validate(validUser));
    }

    @Test
    void shouldAllowBirthdayToday() {
        validUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> validate(validUser));
    }
}