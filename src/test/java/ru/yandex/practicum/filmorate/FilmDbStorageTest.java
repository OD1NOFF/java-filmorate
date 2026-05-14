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
import ru.yandex.practicum.filmorate.storage.film.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.db.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
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
        film.setGenres(new LinkedHashSet<>(Set.of(genre)));

        Film saved = filmStorage.add(film);
        Optional<Film> found = filmStorage.getById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getGenres()).isNotEmpty();
                    assertThat(f.getGenres().iterator().next().getId()).isEqualTo(1);
                });
    }
}