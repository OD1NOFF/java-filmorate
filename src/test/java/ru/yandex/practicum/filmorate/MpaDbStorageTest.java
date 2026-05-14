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
import ru.yandex.practicum.filmorate.model.Mpa;
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
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;
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