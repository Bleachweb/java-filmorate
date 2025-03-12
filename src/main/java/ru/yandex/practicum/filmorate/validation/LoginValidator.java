package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<LoginConstraint, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null) {
            return true;
        }
        return !login.contains(" ");
    }
}