package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sql = """
                INSERT INTO films(name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                statement.setInt(5, film.getMpa().getId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }

            return statement;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        saveGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = """
                UPDATE films
                SET name = ?,
                    description = ?,
                    release_date = ?,
                    duration = ?,
                    mpa_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        saveGenres(film);

        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = """
                SELECT f.*,
                       m.id as mpa_id,
                       m.name as mpa_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                """;

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm);

        return films;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = """
                SELECT f.*,
                       m.id as mpa_id,
                       m.name as mpa_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                WHERE f.id = ?
                """;

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm, id);

        return films.stream().findFirst();
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        String sql = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";

        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genreIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return genreIds.size();
            }
        });
    }

    private Set<Genre> getGenresByFilmId(int filmId) {
        String sql = """
            SELECT g.id, g.name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id = ?
            ORDER BY g.id
            """;

        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = """
                INSERT INTO film_likes(film_id, user_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = """
                DELETE FROM film_likes
                WHERE film_id = ?
                AND user_id = ?
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = """
                SELECT f.*,
                       m.id as mpa_id,
                       m.name as mpa_name
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                LEFT JOIN mpa m ON f.mpa_id = m.id
                GROUP BY f.id, m.id, m.name
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT ?
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
        }

        return films;
    }

    private Film mapFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
        }
        film.setGenres(getGenresByFilmId(film.getId()));
        return film;
    }
}