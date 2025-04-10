package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

public enum MpaRating {
    G("G","У фильма нет возрастных ограничений"),
    PG("PG","Детям рекомендуется смотреть фильм с родителями"),
    PG_13("PG-13","Детям до 13 лет просмотр не желателен"),
    R("R","Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17("NC-17","Лицам до 18 лет просмотр запрещён");

    @Getter
    private final String code;
    private final String description;

    MpaRating(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MpaRating fromString(String code) {
        for (MpaRating rating : MpaRating.values()) {
            if (rating.code.equalsIgnoreCase(code)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг MPA: " + code);
    }
}