package ru.yandex.practicum.filmorate.storage.user.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Override
    public User add(User user) {
        String sql = """
                INSERT INTO users(email, login, name, birthday)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));

            return statement;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE users
                SET email = ?,
                    login = ?,
                    name = ?,
                    birthday = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        return user;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";

        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);

        return users.stream().findFirst();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = """
                INSERT INTO friendships(user_id, friend_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = """
                DELETE FROM friendships
                WHERE user_id = ?
                AND friend_id = ?
                """;

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendships f ON u.id = f.friend_id
                WHERE f.user_id = ?
                """;

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendships f1 ON u.id = f1.friend_id
                JOIN friendships f2 ON u.id = f2.friend_id
                WHERE f1.user_id = ?
                AND f2.user_id = ?
                """;

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}