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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.db.UserDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;
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
}