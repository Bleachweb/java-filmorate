package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public class MpaRatingMapper {
    public static MpaRating mapToMpa(MpaRatingDto mpaDto) {
        return MpaRating.builder()
                .name(mpaDto.getName())
                .description(mpaDto.getDescription())
                .build();
    }

    public static MpaRatingDto mapToMpaRatingDto(MpaRating mpa) {
        return MpaRatingDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .description(mpa.getDescription())
                .build();
    }

    public static List<MpaRatingDto> mapToMpaRatingDtoList(List<MpaRating> mpaList) {
        return mpaList.stream()
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .toList();
    }
}