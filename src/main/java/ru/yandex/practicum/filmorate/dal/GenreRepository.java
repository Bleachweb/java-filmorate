package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_ALL_GENRES_QUERY = """
            SELECT genre_id, name AS genre_name FROM GENRES
            ORDER BY genre_id ASC
            """;

    private static final String GET_GENRE_BY_ID_QUERY = """
            SELECT genre_id, name AS genre_name FROM GENRES
            WHERE genre_id = ?
            """;

    private static final String GET_GENRES_BY_FILM_ID_QUERY = """
            SELECT g.genre_id AS genre_id, g.name AS genre_name, fg.film_id AS film_id
            FROM GENRES AS g
            JOIN FILM_GENRE AS fg ON g.genre_id = fg.genre_id
            WHERE film_id = ?
            """;

    public GenreRepository(JdbcTemplate jdbcTemplate, RowMapper<Genre> genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    public List<Genre> getAllGenres() {
        return getAll(GET_ALL_GENRES_QUERY);
    }

    public Optional<Genre> getGenreById(int genreId) {
        return getOne(GET_GENRE_BY_ID_QUERY, genreId);
    }

}