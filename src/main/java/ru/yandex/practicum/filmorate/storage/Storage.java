package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    T add(T item);
    T update(T item);
    List<T> getAll();
    Optional<T> getById(int id);
}