package ru.yandex.practicum.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> new Post(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("text"),
            List.of(rs.getString("tags").split(",")),
            rs.getInt("likes"),
            rs.getBytes("image")
    );

    public byte[] findImageByPostId(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT image FROM posts WHERE id = ?",
                new Object[]{id},
                byte[].class
        );
    }

    // Получение списка постов с пагинацией
    public List<Post> findAll(int page, int size, String tag) {
        String sql;
        List<Object> params = new ArrayList<>();

        if (tag == null || tag.isEmpty()) {
            sql = "SELECT * FROM posts ORDER BY id DESC LIMIT ? OFFSET ?";
            params.add(size);
            params.add(page * size);
        } else {
            // Построение запроса напрямую с экранированием значения тега
            String escapedTag = tag.replace("'", "''");
            sql = "SELECT * FROM posts WHERE " +
                    "tags = '" + escapedTag + "' OR " +
                    "tags LIKE '" + escapedTag + ",%' OR " +
                    "tags LIKE '%," + escapedTag + ",%' OR " +
                    "tags LIKE '%," + escapedTag + "' " +
                    "ORDER BY id DESC LIMIT ? OFFSET ?";
            params.add(size);
            params.add(page * size);
        }
        return jdbcTemplate.query(sql, postRowMapper, params.toArray());
    }

    public Integer count(String tag) {
        String sql;

        if (tag == null || tag.isEmpty()) {
            sql = "SELECT COUNT(*) FROM posts";
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } else {
            String escapedTag = tag.replace("'", "''");
            sql = "SELECT COUNT(*) FROM posts WHERE " +
                    "tags = '" + escapedTag + "' OR " +
                    "tags LIKE '" + escapedTag + ",%' OR " +
                    "tags LIKE '%," + escapedTag + ",%' OR " +
                    "tags LIKE '%," + escapedTag + "'";
            return jdbcTemplate.queryForObject(sql, Integer.class);
        }
    }

    // Поиск поста по ID
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, id);

        return posts.isEmpty() ? Optional.empty() : Optional.of(posts.getFirst());
    }

    // Сохранение нового поста
    public Post saveAndReturn(Post post) {
        String sql = "INSERT INTO posts (title, text, tags, likes, image) VALUES (?, ?, ?, ?, ?) RETURNING id";

        return jdbcTemplate.queryForObject(sql, new Object[]{
                post.getTitle(),
                post.getText(),
                String.join(",", post.getTags()),
                post.getLikes(),
                post.getImage() != null ? post.getImage() : new byte[0]
        }, (rs, rowNum) -> {
            post.setId(rs.getLong("id"));
            return post;
        });
    }

    // Обновление поста
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, tags = ?, likes = ?, image = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getText(), String.join(",", post.getTags()), post.getLikes(), post.getImage(), post.getId());
    }

    // Удаление поста
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
