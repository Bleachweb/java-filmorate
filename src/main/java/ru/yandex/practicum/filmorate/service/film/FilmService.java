package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private static final int MAX_SIZE_DESCRIPTION = 200;
    private static final LocalDate INTERNATIONAL_FILM_DAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final MpaRatingRepository mpaRepository;
    private final GenreRepository genreRepository;

    public FilmService(@Qualifier("filmRepository") FilmStorage filmStorage,
                       @Qualifier("userRepository") UserStorage userStorage, LikeStorage likeStorage,
                       MpaRatingRepository mpaRepository,
                       GenreRepository genreRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    public FilmDto addFilm(FilmRequest request) {
        FilmRequest updatedRequest = updateFieldsForRequest(request);
        Film film = FilmMapper.mapToFilm(updatedRequest);
        return FilmMapper.mapToFilmDto(filmStorage.addFilm(film));
    }

    public FilmDto updateFilm(FilmRequest request) {
        FilmRequest updatedRequest = updateFieldsForRequest(request);
        Film updatedFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, updatedRequest))
                .orElseThrow(() -> new NotFoundException("Фильм с id " + updatedRequest.getId() + " не найден."));

        return FilmMapper.mapToFilmDto(filmStorage.updateFilm(updatedFilm));
    }

    public List<FilmDto> getAllFilms() {
        return filmStorage.getAllFilms()
                .stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public FilmDto getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));
    }

    public FilmDto addLike(int filmId, int userId) {
        validate(filmId, userId);
        likeStorage.addLike(filmId, userId);
        return getFilmById(filmId);
    }

    public FilmDto removeLike(int filmId, int userId) {
        validate(filmId, userId);
        likeStorage.removeLike(filmId, userId);
        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        return likeStorage.getPopularFilms(count)
                .stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public void validate(int filmId, int userId) {
        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }

        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private FilmRequest updateFieldsForRequest(FilmRequest filmRequest) {
        validateRequest(filmRequest);
        if (filmRequest.getMpa() != null) {
            int mpaId = filmRequest.getMpa().getId();
            filmRequest.setMpa(mpaRepository.getMpaById(mpaId)
                    .orElseThrow(() -> new NotFoundException("Категория с id - " + mpaId + " не найдена")));
        } else {
            filmRequest.setMpa(MpaRating.builder().build());
        }

        if (filmRequest.getGenres() != null) {
            Set<Genre> genres = new LinkedHashSet<>();
            for (Genre genre : filmRequest.getGenres()) {
                int genreId = genre.getId();
                genres.add(genreRepository.getGenreById(genreId)
                        .orElseThrow(() -> new NotFoundException("Жанр с id - " + genreId + " не найден")));
            }
            filmRequest.setGenres(genres);
        } else {
            filmRequest.setGenres(new HashSet<>());
        }

        return filmRequest;
    }

    private static void validateRequest(FilmRequest filmRequest) {
        if (filmRequest.getName() == null || filmRequest.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым!");
        }
        if (filmRequest.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            throw new ValidationException("Максимальная длина описания — " + MAX_SIZE_DESCRIPTION + " символов!");
        }
        if (filmRequest.getReleaseDate().isBefore(INTERNATIONAL_FILM_DAY)) {
            throw new ValidationException("Дата релиза должна быть не раньше " + INTERNATIONAL_FILM_DAY + "!");
        }
        if (filmRequest.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }
}