package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.*;

@Slf4j
@Repository
public class FriendshipRepository extends BaseRepository<Friendship> implements FriendStorage {

    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO FRIENDSHIP
            (user_id, friend_id, status)
            VALUES (?, ?, ?)
            """;

    private static final String GET_FRIENDSHIP_QUERY = """
            SELECT user_id, friend_id
            FROM FRIENDSHIP
            """;

    private static final String GET_FRIENDS_ID_QUERY = """
            SELECT friend_id
            FROM FRIENDSHIP
            WHERE user_id = ?
            """;

    private static final String GET_FRIENDS_QUERY = """
            SELECT * FROM FRIENDSHIP
            WHERE user_id = ?
            AND friend_id = ?
            """;

    private static final String UPDATE_STATUS_QUERY = """
            UPDATE FRIENDSHIP
            SET status = ?
            WHERE friendship_id = ?
            """;

    private static final String DELETE_FRIENDSHIP_QUERY = """
            DELETE FROM FRIENDSHIP
            WHERE user_id = ?
            AND friend_id = ?
            """;

    public FriendshipRepository(JdbcTemplate jdbcTemplate, RowMapper<Friendship> friendshipRowMapper) {
        super(jdbcTemplate, friendshipRowMapper);
    }

    @Override
    public void addFriend(int userId, int friendId, FriendshipStatus status) {
        jdbcTemplate.update(INSERT_FRIEND_QUERY,
                userId,
                friendId,
                status.name());
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        delete(DELETE_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public Map<Integer, Set<Integer>> getFriendship() {
        return jdbcTemplate.query(GET_FRIENDSHIP_QUERY, rs -> {
            Map<Integer, Set<Integer>> result = new HashMap<>();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int friendId = rs.getInt("friend_id");
                result.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
            }
            return result;
        });
    }

    @Override
    public Set<Integer> getFriendsIds(Integer userId) {
        return new HashSet<>(getAsList(GET_FRIENDS_ID_QUERY, userId));
    }

    @Override
    public Integer getFriendshipId(Integer userId, Integer friendId) {
        Optional<Friendship> friendship = getOne(GET_FRIENDS_QUERY, friendId, userId);
        return friendship.map(Friendship::getFriendshipId).orElse(null);
    }

    @Override
    public void updateFriendship(Integer friendshipId, FriendshipStatus status) {
        jdbcTemplate.update(UPDATE_STATUS_QUERY,
                status.name(),
                friendshipId);
    }
}