package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        comment1 = new Comment(null, 1L, "First comment");
        comment2 = new Comment(null, 1L, "Second comment");
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Test
    void testFindByPostId() {
        List<Comment> result = commentRepository.findByPostId(1L);

        assertEquals(2, result.size());
        assertThat(result).extracting(Comment::getText)
                .containsExactlyInAnyOrder("First comment", "Second comment");
    }

    @Test
    void testFindById_WhenCommentExists() {
        Comment savedComment = commentRepository.save(new Comment(null, 2L, "New comment"));

        Optional<Comment> result = commentRepository.findById(savedComment.getId());

        assertTrue(result.isPresent());
        assertEquals("New comment", result.get().getText());
    }

    @Test
    void testFindById_WhenCommentDoesNotExist() {
        Optional<Comment> result = commentRepository.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSave() {
        Comment newComment = new Comment(null, 2L, "Another comment");

        Comment savedComment = commentRepository.save(newComment);

        assertNotNull(savedComment.getId());
        assertEquals("Another comment", savedComment.getText());
    }

    @Test
    void testUpdate() {
        Comment savedComment = commentRepository.save(new Comment(null, 1L, "Old comment"));

        savedComment.setText("Updated comment");
        Comment updatedComment = commentRepository.save(savedComment);

        assertEquals("Updated comment", updatedComment.getText());
    }

    @Test
    void testDelete() {
        Comment savedComment = commentRepository.save(new Comment(null, 2L, "To be deleted"));

        commentRepository.deleteById(savedComment.getId());

        assertFalse(commentRepository.findById(savedComment.getId()).isPresent());
    }

    @Test
    void testDeleteAllByPostId() {
        int deletedCount = commentRepository.deleteAllByPostId(1L);
        assertEquals(2, deletedCount);

        assertTrue(commentRepository.findByPostId(1L).isEmpty());
    }

    @Test
    void testGetAllCommentsByPostIds() {
        commentRepository.save(new Comment(null, 2L, "Comment for post 2"));
        commentRepository.save(new Comment(null, 2L, "Another comment for post 2"));

        List<Comment> result = commentRepository.getAllCommentsByPostIds(List.of(2L));

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllCommentsByPostIds_NullList() {
        List<Comment> result = commentRepository.getAllCommentsByPostIds(null);

        assertTrue(result.isEmpty());
    }
}

