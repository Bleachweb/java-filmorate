package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.List;

@Repository
@Slf4j
public class LikeRepository extends BaseRepository<Like> implements LikeStorage {

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_FILMS_QUERY = """
            SELECT f.film_id, COUNT(l.user_id) AS likes_count
            FROM films AS f
            LEFT JOIN likes AS l ON f.film_id = l.film_id
            GROUP BY f.film_id
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    public LikeRepository(JdbcTemplate jdbcTemplate, RowMapper<Like> likeRowMapper) {
        super(jdbcTemplate, likeRowMapper);
    }

    @Override
    public void addLike(Like like) {
        update(ADD_LIKE_QUERY, like.getFilmId(), like.getUserId());
    }

    @Override
    public void removeLike(Like like) {
        delete(REMOVE_LIKE_QUERY, like.getFilmId(), like.getUserId());
    }

    @Override
    public List<Integer> getPopularFilmIds(int count) {
        return jdbcTemplate.query(GET_POPULAR_FILMS_QUERY,
                (rs, rowNum) -> rs.getInt("film_id"),
                count);
    }

}