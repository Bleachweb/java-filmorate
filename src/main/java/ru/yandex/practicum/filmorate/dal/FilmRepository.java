package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)""";

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?""";

    private static final String GET_ALL_FILM_QUERY = """
            SELECT f.film_id, f.name AS film_name, f.description AS film_description,
                   f.release_date, f.duration,
                   m.mpa_id, m.name AS mpa_name, m.description AS mpa_description,
                   g.genre_id, g.name AS genre_name
            FROM films AS f
            JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            GROUP BY f.film_id, m.mpa_id, g.genre_id
            """;

    private static final String GET_FILM_BY_ID_QUERY = """
            SELECT f.film_id, f.name AS film_name, f.description AS film_description,
                   f.release_date, f.duration,
                   m.mpa_id, m.name AS mpa_name, m.description AS mpa_description,
                   g.genre_id, g.name AS genre_name
            FROM films AS f
            JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            GROUP BY f.film_id, m.mpa_id, g.genre_id
            """;


    public FilmRepository(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper,
                          LikeRepository likeRepository, FilmGenreRepository filmGenreRepository) {
        super(jdbcTemplate, filmRowMapper);
        this.likeRepository = likeRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

    }

    @Override
    public Film addFilm(Film film) {
        Integer id = insertToDatabase(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );
        film.setId(id);
        if (!film.getGenres().isEmpty()) {
            filmGenreRepository.setGenresForFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        filmGenreRepository.setGenresForFilm(film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        return Objects.requireNonNull(jdbcTemplate
                        .query(GET_FILM_BY_ID_QUERY, new FilmExtractor(), filmId))
                .stream().findFirst();
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(GET_ALL_FILM_QUERY, new FilmExtractor());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return likeRepository.getPopularFilmIds(count).stream()
                .map(this::getFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}