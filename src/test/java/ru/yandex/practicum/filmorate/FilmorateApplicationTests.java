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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
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
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

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

    // ==================== FILM TESTS ====================

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film saved = filmStorage.add(film);
        assertThat(saved.getId()).isPositive();

        Optional<Film> found = filmStorage.getById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getMpa().getId()).isEqualTo(1);
                });
    }

    @Test
    void testGetAllFilms() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        filmStorage.add(film);

        List<Film> films = filmStorage.getAll();
        assertThat(films).isNotEmpty();
    }

    @Test
    void testGetFilmByIdNotFound() {
        Optional<Film> film = filmStorage.getById(999);
        assertThat(film).isEmpty();
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Original");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        Film saved = filmStorage.add(film);

        saved.setName("Updated");
        filmStorage.update(saved);

        Optional<Film> updated = filmStorage.getById(saved.getId());
        assertThat(updated).isPresent()
                .hasValueSatisfying(f -> assertThat(f.getName()).isEqualTo("Updated"));
    }

    @Test
    void testAddAndRemoveLike() {
        Film film = new Film();
        film.setName("Liked Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        Film saved = filmStorage.add(film);

        filmStorage.addLike(saved.getId(), 1);
        List<Film> popular = filmStorage.getPopular(10);
        assertThat(popular).extracting(Film::getId).contains(saved.getId());

        filmStorage.removeLike(saved.getId(), 1);
    }

    @Test
    void testGetPopular() {
        List<Film> popular = filmStorage.getPopular(10);
        assertThat(popular).isNotNull();
    }

    @Test
    void testAddFilmWithGenres() {
        Film film = new Film();
        film.setName("Film with genres");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(List.of(genre));

        Film saved = filmStorage.add(film);
        Optional<Film> found = filmStorage.getById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getGenres()).isNotEmpty();
                    assertThat(f.getGenres().get(0).getId()).isEqualTo(1);
                });
    }

    // ==================== GENRE TESTS ====================

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
    }

    @Test
    void testGetGenreById() {
        Optional<Genre> genre = genreStorage.getById(1);
        assertThat(genre).isPresent()
                .hasValueSatisfying(g -> assertThat(g.getName()).isEqualTo("Комедия"));
    }

    @Test
    void testGetGenreByIdNotFound() {
        Optional<Genre> genre = genreStorage.getById(999);
        assertThat(genre).isEmpty();
    }

    // ==================== MPA TESTS ====================

    @Test
    void testGetAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAll();
        assertThat(mpaList).hasSize(5);
    }

    @Test
    void testGetMpaById() {
        Optional<Mpa> mpa = mpaStorage.getById(1);
        assertThat(mpa).isPresent()
                .hasValueSatisfying(m -> assertThat(m.getName()).isEqualTo("G"));
    }

    @Test
    void testGetMpaByIdNotFound() {
        Optional<Mpa> mpa = mpaStorage.getById(999);
        assertThat(mpa).isEmpty();
    }
}