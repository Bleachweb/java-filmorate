package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private static final int MAX_SIZE_DESCRIPTION = 200;
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = MAX_SIZE_DESCRIPTION, message = "Максимальная длина описания — " + MAX_SIZE_DESCRIPTION + " символов.")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой.")
    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private int duration;

    private MpaRating mpa;

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

}