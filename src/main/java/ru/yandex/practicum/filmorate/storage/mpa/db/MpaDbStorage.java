package ru.yandex.practicum.filmorate.storage.mpa.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaRowMapper mpaRowMapper = new MpaRowMapper();

    @Override
    public List<Mpa> getAll() {
        String sql = """
                SELECT *
                FROM mpa
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = """
                SELECT *
                FROM mpa
                WHERE id = ?
                """;

        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper, id);

        return mpaList.stream().findFirst();
    }
}