
package ru.yandex.practicum.filmorate.service.film;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.storage.film.MpaRatingStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaRatingService {
    private final MpaRatingStorage mpaStorage;

    public MpaRatingDto getMPAById(Integer mpaId) {
        return mpaStorage.getMpaById(mpaId)
                .map(MpaRatingMapper::mapToMpaRatingDto)
                .orElseThrow(() -> new NotFoundException("Mpa с id - " + mpaId + " не найден"));
    }

    public List<MpaRatingDto> getAllMPAs() {
        return MpaRatingMapper.mapToMpaRatingDtoList(mpaStorage.getAllMPAs());
    }
}
