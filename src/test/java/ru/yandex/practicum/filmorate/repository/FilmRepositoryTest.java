package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@ComponentScan("ru/yandex/practicum/filmorate/*")
class FilmRepositoryTest {
    private final FilmRepository filmStorage;
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
    }

    @Test
    public void testAddFilm() {
        filmStorage.addFilm(newFilm);
        final int filmId = newFilm.getId();

        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", filmId));
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.addFilm(newFilm);
        newFilm.setName("Updated film");
        filmStorage.updateFilm(newFilm);
        final int filmId = newFilm.getId();

        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Updated film"));
    }

    @Test
    public void testGetFilmById() {
        filmStorage.addFilm(newFilm);
        final int filmId = newFilm.getId();

        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", filmId));
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.addFilm(newFilm);
        filmStorage.addFilm(newFilm);

        Collection<Film> films = filmStorage.getAllFilms();

        assertThat(films.size()).isEqualTo(2);
    }
}