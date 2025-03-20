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

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenEmailIsBlank_thenValidationFails() {
        User user = new User();
        user.setEmail("");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenLoginIsBlank_thenValidationFails() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenLoginContainsSpaces_thenValidationFails() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenBirthdayIsInFuture_thenValidationFails() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}