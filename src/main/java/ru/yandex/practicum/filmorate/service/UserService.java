package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    public void addFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId).isEmpty() || userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " или " + friendId + " не найден.");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId).isEmpty() || userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " или " + friendId + " не найден.");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}