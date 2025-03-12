package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDescriptionIsTooLong_thenValidationFails() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("a".repeat(201)); // 201 characters
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenReleaseDateIsNull_thenValidationFails() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid Description");
        film.setReleaseDate(null);
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenDurationIsNegative_thenValidationFails() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}