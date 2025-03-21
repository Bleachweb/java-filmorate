package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate FIRS_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {

        if (date == null) {
            return true;
        }

        return !date.isBefore(FIRS_FILM_RELEASE);
    }
}