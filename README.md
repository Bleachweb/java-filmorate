# java-filmorate

Template repository for Filmorate project.

![DB schema](/schema.png)

## Примеры SQL-запросов:

### <ins>Получение общих друзей:</ins>

<details>
<summary>Таблица users</summary> 

| user_id | email             | login | name | birthday   |
|---------|-------------------|-------|------|------------|
| 1       | user1@example.com | user1 | Ваня | 1990-01-15 |
| 2       | user2@example.com | user2 | Даша | 1995-05-20 |
| 3       | user3@example.com | user3 | Катя | 1985-11-03 |

</details>

<details>
<summary> Таблица friendships</summary> 

| user_id | friend_id | status    |
|---------|-----------|-----------|
| 1       | 2         | CONFIRMED |
| 1       | 3         | PENDING   |
| 2       | 3         | CONFIRMED |

</details>

```ruby
SELECT u.*
FROM users u
JOIN friendships f1 ON u.user_id = f1.friend_id AND f1.user_id = 1 AND f1.status = 'CONFIRMED'
JOIN friendships f2 ON u.user_id = f2.friend_id AND f2.user_id = 2 AND f2.status = 'CONFIRMED';
```

| user_id | email             | login | name | birthday   |
|---------|-------------------|-------|------|------------|
| 3       | user3@example.com | user3 | Катя | 1985-11-03 |

### <ins>Получение жанров для фильма по film_id:</ins>

<details>
<summary> Таблица films</summary>  

| film_id | name   | description      | release_date | duration | mpa_code |
|---------|--------|------------------|--------------|----------|----------|
| 1       | first  | some description | 01/01/2025   | 90       | G        |
| 2       | second | some description | 28/03/1995   | 90       | PG       |
| 3       | third  | some description | 13/08/2004   | 120      | R        |   

</details>

<details>
<summary> Таблица genres</summary>   

| genre_id | name           |
|----------|----------------|
| 1        | Комедия        |
| 2        | Драма          |
| 3        | Мультфильм     |
| 4        | Триллер        |
| 5        | Документальный |
| 6        | Боевик         |

</details>

<details>
<summary> Таблица film_genres</summary>   

| film_id | genre_id |
|---------|----------|
| 1       | 4        |
| 1       | 6        |
| 2       | 3        |
| 2       | 1        |
| 3       | 2        |

</details>

```ruby
SELECT g.name
FROM genres g
JOIN film_genres fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = 1;
```

| name    |
|---------|
| Триллер |
| Боевик  |

### <ins>Получение топ-3 популярных фильмов:</ins>

```ruby
SELECT  f.*, COUNT(l.user_id) AS likes_count
FROM    films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT 3;
```

| film_id | name   | description      | release_date | duration | mpa_code | likes_count |
|---------|--------|------------------|--------------|----------|----------|-------------|
| 1       | first  | some description | 01/01/2025   | 90       | G        | 20          |
| 2       | second | some description | 28/03/1995   | 90       | PG       | 18          |
| 3       | third  | some description | 13/08/2004   | 120      | R        | 15          |

