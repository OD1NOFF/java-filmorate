package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {
    void addFriend(int userId, int friendId);
    void removeFriend(int userId, int friendId);
    List<User> getFriends(int userId);
    List<User> getCommonFriends(int userId, int otherId);
}