package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Map;
import java.util.Set;

public interface FriendStorage {

    void addFriend(int userId, int friendId, FriendshipStatus isFriend);

    void removeFriend(int userId, int friendId);

    Map<Integer, Set<Integer>> getFriendship();

    Set<Integer> getFriendsIds(Integer userId);

    Integer getFriendshipId(Integer userId, Integer friendId);

    void updateFriendship(Integer friendshipId, FriendshipStatus isFriend);
}