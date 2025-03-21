package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userStorage.getUserById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + user.getId() + " не найден."));

        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден."));

        friendStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден."));

        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return friendStorage.getCommonFriends(userId, otherId);
    }
}