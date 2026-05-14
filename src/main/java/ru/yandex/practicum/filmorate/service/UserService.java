package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User update(User user) {
        getById(user.getId()); // проверяем существование
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);

        userStorage.addFriend(userId, friendId);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);

        userStorage.removeFriend(userId, friendId);

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getById(userId);

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        getById(userId);
        getById(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }
}