package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    void addLike(Like like);

    void removeLike(Like like);

    List<Integer> getPopularFilmIds(int count);
}