package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userRepository") UserStorage userStorage,
                       FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public UserDto addUser(UserRequest request) {
        User user = UserMapper.mapToUser(request);
        if (user.getName() == null) {
            user.setName(user.getLogin().trim());
        }
        return UserMapper.mapToUserDto(userStorage.addUser(user));
    }

    public UserDto updateUser(UserRequest request) {
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + request.getId() + " не найден."));
        UserDto userDto = UserMapper.mapToUserDto(userStorage.updateUser(updatedUser));
        return userDto;
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = UserMapper.mapToUserDtoList(userStorage.getAllUsers());

        Map<Integer, Set<Integer>> friendsByUserId = friendStorage.getFriendship();

        usersDto.forEach(userDto ->
                userDto.setFriends(friendsByUserId.getOrDefault(userDto.getId(), Set.of()))
        );

        return usersDto;
    }

    public UserDto getUserById(int id) {
        UserDto userDto = checkUser(id);
        userDto.setFriends(friendStorage.getFriendsIds(id));
        return userDto;
    }

    public void addFriend(int userId, int friendId) {
        checkUser(userId);
        checkUser(friendId);
        FriendshipStatus status = validateFriendship(userId, friendId);
        friendStorage.addFriend(userId, friendId, status);
    }

    public void removeFriend(int userId, int friendId) {
        checkUser(userId);
        checkUser(friendId);
        friendStorage.removeFriend(userId, friendId);
        Integer friendshipId = friendStorage.getFriendshipId(userId, friendId);
        friendStorage.updateFriendship(friendshipId, FriendshipStatus.NOT_FRIEND);
    }

    public List<UserDto> getFriends(int userId) {
        checkUser(userId);
        return UserMapper.mapToUserDtoList(userStorage.getUserFriends(userId));
    }

    public List<UserDto> getCommonFriends(int userId, int otherId) {
        return UserMapper.mapToUserDtoList(userStorage.getCommonFriends(userId, otherId));
    }

    private UserDto checkUser(int id) {
        return userStorage.getUserById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    private FriendshipStatus validateFriendship(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new InternalServerException("Нельзя себя добавить в друзья");
        }

        Set<Integer> userFriendsIds = friendStorage.getFriendsIds(userId);
        Set<Integer> friendFriendsIds = friendStorage.getFriendsIds(friendId);

        if (userFriendsIds.contains(friendId)) {
            throw new DuplicateFoundException("Пользователь с id - " + friendId + " , уже добавлен в друзья");
        }

        if (friendFriendsIds.contains(userId)) {
            Integer friendshipId = friendStorage.getFriendshipId(userId, friendId);
            friendStorage.updateFriendship(friendshipId, FriendshipStatus.FRIEND);
            return FriendshipStatus.FRIEND;
        }
        return FriendshipStatus.NOT_FRIEND;
    }

}
