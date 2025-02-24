package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.model.Comment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CommentRepository commentRepository;

    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    private Comment comment1;
    private Comment comment2;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        comment1 = new Comment(1L, 1L, "First comment");
        comment2 = new Comment(2L, 1L, "Second comment");
        comments = Arrays.asList(comment1, comment2);
    }

    @Test
    void testFindByPostId() {
        Long postId = 1L;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(postId)))
                .thenReturn(comments);

        List<Comment> result = commentRepository.findByPostId(postId);

        assertEquals(2, result.size());
        assertEquals(comment1, result.get(0));
        assertEquals(comment2, result.get(1));
        verify(jdbcTemplate).query(contains("SELECT * FROM comments WHERE post_id = ?"), any(RowMapper.class), eq(postId));
    }

    @Test
    void testFindById_WhenCommentExists() {
        Long commentId = 1L;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(commentId)))
                .thenReturn(Collections.singletonList(comment1));

        Optional<Comment> result = commentRepository.findById(commentId);

        assertTrue(result.isPresent());
        assertEquals(comment1, result.get());
        verify(jdbcTemplate).query(contains("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(commentId));
    }

    @Test
    void testFindById_WhenCommentDoesNotExist() {
        Long commentId = 99L;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(commentId)))
                .thenReturn(Collections.emptyList());

        Optional<Comment> result = commentRepository.findById(commentId);

        assertFalse(result.isPresent());
        verify(jdbcTemplate).query(contains("SELECT * FROM comments WHERE id = ?"), any(RowMapper.class), eq(commentId));
    }

    @Test
    void testSave() {
        Comment newComment = new Comment(null, 1L, "New comment");

        commentRepository.save(newComment);

        verify(jdbcTemplate).update(
                eq("INSERT INTO comments (post_id, text) VALUES (?, ?)"),
                eq(newComment.getPostId()),
                eq(newComment.getText())
        );
    }

    @Test
    void testUpdate() {
        Comment updatedComment = new Comment(1L, 1L, "Updated comment");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L)))
                .thenReturn(Collections.singletonList(updatedComment));

        Comment result = commentRepository.update(updatedComment);

        verify(jdbcTemplate).update(
                eq("UPDATE comments SET text = ? WHERE id = ?"),
                eq(updatedComment.getText()),
                eq(updatedComment.getId())
        );
        assertEquals(updatedComment, result);
    }

    @Test
    void testDelete() {
        Long commentId = 1L;

        commentRepository.delete(commentId);

        verify(jdbcTemplate).update(eq("DELETE FROM comments WHERE id = ?"), eq(commentId));
    }

    @Test
    void testDeleteAllByPostId() {
        Long postId = 1L;
        when(jdbcTemplate.update(anyString(), eq(postId))).thenReturn(2);

        int deletedCount = commentRepository.deleteAllByPostId(postId);

        assertEquals(2, deletedCount);
        verify(jdbcTemplate).update(eq("DELETE FROM comments WHERE post_id = ?"), eq(postId));
    }

    @Test
    void testGetAllCommentsByPostIds() {
        List<Long> postIds = Arrays.asList(1L, 2L);
        Comment comment1ForPost1 = new Comment(1L, 1L, "Comment 1 for post 1");
        Comment comment2ForPost1 = new Comment(2L, 1L, "Comment 2 for post 1");
        Comment comment1ForPost2 = new Comment(3L, 2L, "Comment 1 for post 2");

        List<Comment> allComments = Arrays.asList(comment1ForPost1, comment2ForPost1, comment1ForPost2);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(Object[].class)))
                .thenReturn(allComments);

        Map<Long, List<Comment>> result = commentRepository.getAllCommentsByPostIds(postIds);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertEquals(2, result.get(1L).size());
        assertEquals(1, result.get(2L).size());

        verify(jdbcTemplate).query(contains("SELECT * FROM comments WHERE post_id IN (?,?)"),
                any(RowMapper.class),
                argsCaptor.capture());

        Object[] capturedArgs = argsCaptor.getValue();
        assertEquals(2, capturedArgs.length);
        assertEquals(1L, capturedArgs[0]);
        assertEquals(2L, capturedArgs[1]);
    }

    @Test
    void testGetAllCommentsByPostIds_EmptyList() {
        List<Long> postIds = Collections.emptyList();

        Map<Long, List<Comment>> result = commentRepository.getAllCommentsByPostIds(postIds);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), any(Object[].class));
    }

    @Test
    void testGetAllCommentsByPostIds_NullList() {
        List<Long> postIds = null;

        Map<Long, List<Comment>> result = commentRepository.getAllCommentsByPostIds(postIds);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), any(Object[].class));
    }
}
