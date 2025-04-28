package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;


    public FilmService(@Qualifier("filmRepository") FilmStorage filmStorage,
                       @Qualifier("userRepository") UserStorage userStorage, LikeStorage likeStorage,
                       MpaRatingRepository mpaRepository, FilmGenreRepository filmGenreRepository,
                       LikeRepository likeRepository, GenreRepository genreRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.likeRepository = likeRepository;

    }

    public FilmDto addFilm(FilmRequest request) {
        FilmRequest updatedRequest = updateFieldsForRequest(request);
        Film film = FilmMapper.mapToFilm(updatedRequest);
        Film savedFilm = filmStorage.addFilm(film);

        if (!savedFilm.getGenres().isEmpty()) {
            filmGenreRepository.setGenresForFilm(savedFilm);
        }

        return FilmMapper.mapToFilmDto(savedFilm);
    }

    public FilmDto updateFilm(FilmRequest request) {
        FilmRequest updatedRequest = updateFieldsForRequest(request);
        Film updatedFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, updatedRequest))
                .orElseThrow(() -> new NotFoundException("Фильм с id " + updatedRequest.getId() + " не найден."));

        Film result = filmStorage.updateFilm(updatedFilm);
        filmGenreRepository.setGenresForFilm(result);
        return FilmMapper.mapToFilmDto(result);
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
        likeStorage.addLike(new Like(filmId, userId));
        return getFilmById(filmId);
    }

    public FilmDto removeLike(int filmId, int userId) {
        validate(filmId, userId);
        likeStorage.removeLike(new Like(filmId, userId));
        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Integer> popularFilmIds = likeRepository.getPopularFilmIds(count);
        return popularFilmIds.stream()
                .map(filmStorage::getFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public void validate(int filmId, int userId) {
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));

        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    private FilmRequest updateFieldsForRequest(FilmRequest filmRequest) {
        validateRequest(filmRequest);
        filmRequest.setMpa(processMpa(filmRequest.getMpa()));
        filmRequest.setGenres(processGenres(filmRequest.getGenres()));
        return filmRequest;
    }

    private MpaRating processMpa(MpaRating mpa) {
        if (mpa == null) {
            return MpaRating.builder().build();
        }
        int mpaId = mpa.getId();
        return mpaRepository.getMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Категория с id - " + mpaId + " не найдена"));
    }

    private Set<Genre> processGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return new HashSet<>();
        }

        Set<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Map<Integer, Genre> existingGenres = genreRepository.getGenresByIds(genreIds)
                .stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        Set<Integer> notFoundIds = genreIds.stream()
                .filter(id -> !existingGenres.containsKey(id))
                .collect(Collectors.toSet());

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Жанры с id " + notFoundIds + " не найдены");
        }

        return new LinkedHashSet<>(existingGenres.values());
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