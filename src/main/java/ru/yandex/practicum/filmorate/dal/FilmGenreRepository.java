package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> implements FilmGenreStorage {

    private static final String ADD_FILM_GENRE_QUERY = """
            INSERT INTO
            FILM_GENRE (film_id, genre_id)
            VALUES(?, ?)
            """;

    private static final String UPDATE_FILM_GENRE_QUERY = """
            UPDATE FILM_GENRE
            SET genre_id = ?
            WHERE film_id = ?
            """;

    private static final String GET_GENRES_FROM_FILM_QUERY = """
            SELECT fg.* FROM FILM_GENRE AS fg
            LEFT JOIN GENRES AS g
            ON fg.genre_id = g.genre_id
            WHERE film_id = ?
            """;

    private static final String DELETE_GENRES_FROM_FILM = """
            DELETE FROM FILM_GENRE
            WHERE film_id = ?
            """;

    public FilmGenreRepository(JdbcTemplate jdbcTemplate, RowMapper<FilmGenre> filmGenreRowMapper) {
        super(jdbcTemplate, filmGenreRowMapper);
    }

    @Override
    public void addFilmGenre(Integer filmId, Integer genreId) {
        insertToDatabase(ADD_FILM_GENRE_QUERY, filmId, genreId);
    }

    @Override
    public void updateFilmGenres(Integer filmId, Integer genreId) {
        update(UPDATE_FILM_GENRE_QUERY, filmId, genreId);
    }

    @Override
    public List<FilmGenre> getGenresForFilm(Integer filmId) {
        return getAll(GET_GENRES_FROM_FILM_QUERY, filmId);
    }

    @Override
    public void deleteGenresForFilm(Integer filmId) {
        delete(DELETE_GENRES_FROM_FILM, filmId);
    }

    public void setGenresForFilm(Film film) {
        if (film.getGenres() != null) {
            HashSet<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toCollection(HashSet::new));
            batchUpdate(new ArrayList<>(genreIds), ADD_FILM_GENRE_QUERY, film.getId());
        }
    }
}