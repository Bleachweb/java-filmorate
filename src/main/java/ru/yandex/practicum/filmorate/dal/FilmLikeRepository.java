package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;

@Slf4j
@Repository
public class FilmLikeRepository extends BaseRepository<FilmLike> {

    private static final String ADD_LIKE_QUERY = """
            INSERT INTO LIKES (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String GET_FILM_IDS_BY_USER_ID_QUERY = "SELECT film_id FROM LIKES WHERE user_id = ?";

    private static final String GET_USER_IDS_BY_FILM_ID_QUERY = "SELECT user_id FROM LIKES  WHERE film_id = ?";

    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM LIKES
            WHERE film_id = ?
            AND user_id = ?
            """;

    public FilmLikeRepository(JdbcTemplate jdbcTemplate, RowMapper<FilmLike> likeRowMapper) {
        super(jdbcTemplate, likeRowMapper);
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        insertToDatabase(ADD_LIKE_QUERY, filmId, userId);
    }

    public List<Integer> getFilmIdsByUserId(Integer userId) {
        return getAsList(GET_FILM_IDS_BY_USER_ID_QUERY, userId);
    }

    public List<Integer> getUserIdsByFilmId(Integer filmId) {
        return getAsList(GET_USER_IDS_BY_FILM_ID_QUERY, filmId);
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        delete(DELETE_LIKE_QUERY, filmId, userId);
    }
}