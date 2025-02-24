package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Маппер для преобразования строк из БД в объект Comment
    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getString("text")
    );

    // Получение списка комментариев к посту
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    // Поиск комментария по ID
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, id);
        return comments.isEmpty() ? Optional.empty() : Optional.of(comments.get(0));
    }

    // Сохранение нового комментария
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, text) VALUES (?, ?)";
        jdbcTemplate.update(sql, comment.getPostId(), comment.getText());
    }

    // Обновление комментария
    public Comment update(Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getId());

        return this.findById(comment.getId()).orElseThrow();
    }

    // Удаление комментария по id
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Удаление комментария по id поста
    public int deleteAllByPostId(Long postId) {
        String sql = "DELETE FROM comments WHERE post_id = ?";
        return jdbcTemplate.update(sql, postId);
    }


    // Получение всех комментариев по списку идентификаторов постов
    public Map<Long, List<Comment>> getAllCommentsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = "SELECT * FROM comments WHERE post_id IN (" +
                postIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ") ORDER BY id ASC";

        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, postIds.toArray());

        return comments.stream().collect(Collectors.groupingBy(Comment::getPostId));
    }
}

