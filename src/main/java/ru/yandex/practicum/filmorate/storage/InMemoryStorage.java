package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Identifiable;

import java.util.*;

public class InMemoryStorage<T extends Identifiable> {

    private final Map<Integer, T> items = new HashMap<>();

    private int idCounter = 1;

    public T add(T item) {
        item.setId(idCounter++);
        items.put(item.getId(), item);
        return item;
    }

    public T update(T item) {
        items.put(item.getId(), item);
        return item;
    }

    public List<T> getAll() {
        return new ArrayList<>(items.values());
    }

    public Optional<T> getById(int id) {
        return Optional.ofNullable(items.get(id));
    }
}