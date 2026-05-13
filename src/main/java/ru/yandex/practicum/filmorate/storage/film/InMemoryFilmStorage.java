package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Collections;
import java.util.List;

@Component
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void removeLike(int filmId, int userId) {

    }

    @Override
    public List<Film> getPopular(int count) {
        return Collections.emptyList();
    }
}