package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.Getter;
import ru.yandex.practicum.filmorate.validation.LoginConstraint;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна содержать символ @.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @LoginConstraint
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    @Getter
    private Map<Integer, FriendshipStatus> friends = new HashMap<>();

    public void addFriend(int friendId, FriendshipStatus status) {
        friends.put(friendId, status);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}