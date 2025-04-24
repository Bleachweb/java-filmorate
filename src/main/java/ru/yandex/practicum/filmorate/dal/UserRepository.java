package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {

    private static final String CREATE_USER_QUERY = """
            INSERT INTO USERS
            (login, email, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_USER_QUERY = """
            UPDATE USERS
            SET login = ?, email = ?, name = ?, birthday = ?
            WHERE user_id = ?
            """;

    private static final String DELETE_USER_QUERY = """
            DELETE FROM USERS
            WHERE user_id = ?
            """;

    private static final String GET_ALL_USERS_QUERY = """
            SELECT * FROM USERS
            """;

    private static final String GET_USER_BY_ID_QUERY = GET_ALL_USERS_QUERY + """
            WHERE user_id = ?
            """;

    private static final String GET_USER_BY_LOGIN_QUERY = GET_ALL_USERS_QUERY + """
            WHERE login = ?
            """;

    private static final String GET_USER_BY_EMAIL_QUERY = GET_ALL_USERS_QUERY + """
            WHERE email = ?
            """;

    private static final String GET_USER_FRIENDS_QUERY = GET_ALL_USERS_QUERY + """
            WHERE user_id IN (
            SELECT friend_id
            FROM FRIENDSHIP
            WHERE user_id = ?)
            """;

    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT * FROM USERS u
            JOIN FRIENDSHIP f
            ON u.user_id = f.friend_id
            WHERE f.user_id = ?
            AND f.friend_id
            IN (SELECT friend_id
            FROM FRIENDSHIP
            WHERE user_id = ?)
            """;

    public UserRepository(JdbcTemplate jdbcTemplate, RowMapper<User> userRowMapper) {
        super(jdbcTemplate, userRowMapper);
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin().trim());
        }
        Integer id = insertToDatabase(
                CREATE_USER_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_USER_QUERY,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return getOne(GET_USER_BY_ID_QUERY, userId);
    }

    @Override
    public List<User> getAllUsers() {
        return getAll(GET_ALL_USERS_QUERY);
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        if (getOne(GET_USER_BY_ID_QUERY, userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        return getAll(GET_USER_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return getAll(GET_COMMON_FRIENDS_QUERY, userId, otherId);
    }
}