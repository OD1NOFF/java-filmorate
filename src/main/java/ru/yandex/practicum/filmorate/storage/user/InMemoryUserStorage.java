package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Collections;
import java.util.List;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public void removeFriend(int userId, int friendId) {

    }

    @Override
    public List<User> getFriends(int userId) {
        return Collections.emptyList();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return Collections.emptyList();
    }
}