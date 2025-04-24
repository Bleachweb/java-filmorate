package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.*;

@Slf4j
@Repository
public class FriendshipRepository extends BaseRepository<Friendship> implements FriendStorage {

    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO FRIENDSHIP
            (user_id, friend_id, is_friend)
            VALUES (?, ?, ?)
            """;

    private static final String GET_FRIENDSHIP_QUERY = """
            SELECT *
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

    private static final String UPDATE_IS_FRIENDS_QUERY = """
            UPDATE FRIENDSHIP
            SET is_friend = ?
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
    public void addFriend(int userId, int friendId, boolean isFriend) {
        insertToDatabase(INSERT_FRIEND_QUERY, userId, friendId, isFriend);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        delete(DELETE_FRIENDSHIP_QUERY, userId, friendId);
    }

    public List<Friendship> getFriendship() {
        return getAll(GET_FRIENDSHIP_QUERY);
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
    public void updateFriendship(Integer friendshipId, Boolean isFriend) {
        update(UPDATE_IS_FRIENDS_QUERY, isFriend, friendshipId);
    }
}