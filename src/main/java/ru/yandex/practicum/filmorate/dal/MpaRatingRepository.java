package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.MpaRatingStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaRatingRepository extends BaseRepository<MpaRating> implements MpaRatingStorage {
    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM MPA";
    private static final String GET_MPA_BY_ID_QUERY = GET_ALL_MPA_QUERY + " WHERE mpa_id = ?";

    public MpaRatingRepository(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> mpaRowMapper) {
        super(jdbcTemplate, mpaRowMapper);
    }

    @Override
    public Optional<MpaRating> getMpaById(Integer mpaId) {
        return getOne(GET_MPA_BY_ID_QUERY, mpaId);
    }

    @Override
    public List<MpaRating> getAllMPAs() {
        return getAll(GET_ALL_MPA_QUERY);
    }
}