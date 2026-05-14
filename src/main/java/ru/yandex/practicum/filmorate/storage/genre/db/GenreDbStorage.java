package ru.yandex.practicum.filmorate.storage.genre.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreRowMapper genreRowMapper = new GenreRowMapper();

    @Override
    public List<Genre> getAll() {
        String sql = """
                SELECT *
                FROM genres
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = """
                SELECT *
                FROM genres
                WHERE id = ?
                """;

        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, id);

        return genres.stream().findFirst();
    }

    public List<Genre> findByIds(Set<Integer> ids) {
        String inClause = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String sql = "SELECT * FROM genres WHERE id IN (" + inClause + ")";
        return jdbcTemplate.query(sql, genreRowMapper);
    }
}