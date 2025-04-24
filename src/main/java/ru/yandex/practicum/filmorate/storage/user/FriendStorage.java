package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Set;

public interface FriendStorage {

    void addFriend(int userId, int friendId, boolean isFriend);

    void removeFriend(int userId, int friendId);

    List<Friendship> getFriendship();

    Set<Integer> getFriendsIds(Integer userId);

    Integer getFriendshipId(Integer userId, Integer friendId);

    void updateFriendship(Integer friendshipId, Boolean isFriend);
}