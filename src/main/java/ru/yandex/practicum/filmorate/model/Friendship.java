package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Friendship {
    private Integer friendshipId;
    private Integer userId;
    private Integer friendId;
    private Boolean isFriend;
}