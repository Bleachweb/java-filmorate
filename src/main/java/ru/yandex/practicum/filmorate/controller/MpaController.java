package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.service.film.MpaRatingService;

import java.util.Collection;

@Slf4j
@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaRatingService mpaService;

    @GetMapping
    public Collection<MpaRatingDto> getAllMPAs() {
        return mpaService.getAllMPAs();
    }

    @GetMapping("/{id}")
    public MpaRatingDto getMPAById(@PathVariable int id) {
        return mpaService.getMPAById(id);
    }
}