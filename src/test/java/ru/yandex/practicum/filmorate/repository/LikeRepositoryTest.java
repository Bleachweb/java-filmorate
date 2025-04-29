package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
class LikeRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final UserRepository userStorage;
    private final FilmRepository filmStorage;
    private final LikeRepository likeStorage;
    Film newFilm;
    User newUser;

    @BeforeEach
    public void setUp() {
        newFilm = Film.builder()
                .name("Test film")
                .description("Test description")
                .duration(100)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(MpaRating.builder()
                        .id(1)
                        .build())
                .build();

        newUser = User.builder()
                .login("Test user")
                .email("test@test.com")
                .name("Test user")
                .birthday(LocalDate.of(1980, 12, 12))
                .build();

        filmStorage.addFilm(newFilm);
        userStorage.addUser(newUser);
    }

    @Test
    public void testAddLikeFilm() {
        final int filmId = newFilm.getId();
        final int userId = newUser.getId();
        likeStorage.addLike(new Like(filmId, userId));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                filmId, userId);

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testRemoveLikeFilm() {
        final int filmId = newFilm.getId();
        final int userId = newUser.getId();
        likeStorage.addLike(new Like(filmId, userId));
        likeStorage.removeLike(new Like(filmId, userId));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                filmId, userId);

        assertThat(count).isEqualTo(0);
    }

    @Test
    public void testGetPopularFilms() {
        Film secondFilm = Film.builder()
                .name("Second film")
                .description("Second description")
                .duration(120)
                .releaseDate(LocalDate.of(2001, 12, 12))
                .mpa(MpaRating.builder()
                        .id(2)
                        .build())
                .build();
        filmStorage.addFilm(secondFilm);
        final int filmId = newFilm.getId();
        final int userId = newUser.getId();
        likeStorage.addLike(new Like(filmId, userId));

        List<Integer> popularFilmIds = likeStorage.getPopularFilmIds(2);

        assertThat(popularFilmIds).asList().hasSize(2);
    }
}