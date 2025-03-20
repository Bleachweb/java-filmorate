package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnBadRequestWhenFilmNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestWhenFilmDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("a".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestWhenFilmDurationIsNegative() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}