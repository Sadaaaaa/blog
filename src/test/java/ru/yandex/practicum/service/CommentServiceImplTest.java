package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl commentService;

    @MockitoSpyBean
    private CommentRepository commentRepository;

    private Comment testComment;
    private final Long TEST_POST_ID = 1L;
    private final Long TEST_COMMENT_ID = 1L;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        testComment = new Comment();
        testComment.setId(TEST_COMMENT_ID);
        testComment.setPostId(TEST_POST_ID);
        testComment.setText("Test comment");
    }

    @Test
    void findByPostId_ShouldReturnListOfComments() {
        List<Comment> expectedComments = Arrays.asList(testComment);
        when(commentRepository.findByPostId(TEST_POST_ID)).thenReturn(expectedComments);

        List<Comment> actualComments = commentService.findByPostId(TEST_POST_ID);

        assertNotNull(actualComments);
        assertEquals(expectedComments.size(), actualComments.size());
        assertEquals(expectedComments.get(0).getText(), actualComments.get(0).getText());
        verify(commentRepository).findByPostId(TEST_POST_ID);
    }

    @Test
    void findById_WhenCommentExists_ShouldReturnComment() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.findById(TEST_COMMENT_ID);

        assertTrue(result.isPresent());
        assertEquals(testComment.getText(), result.get().getText());
        verify(commentRepository).findById(TEST_COMMENT_ID);
    }

    @Test
    void findById_WhenCommentDoesNotExist_ShouldReturnEmpty() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.empty());

        Optional<Comment> result = commentService.findById(TEST_COMMENT_ID);

        assertFalse(result.isPresent());
        verify(commentRepository).findById(TEST_COMMENT_ID);
    }

    @Test
    void addComment_ShouldSaveComment() {
        Comment newComment = new Comment();
        newComment.setText("New comment");

        commentService.addComment(TEST_POST_ID, newComment);

        assertEquals(TEST_POST_ID, newComment.getPostId());
        verify(commentRepository).save(newComment);
    }

    @Test
    void updateComment_WhenCommentExists_ShouldUpdateAndReturnComment() {
        // 1. Создаем и сохраняем комментарий в базу
        Comment existingComment = new Comment(null, 1L, "Original text");
        existingComment = commentRepository.save(existingComment);

        // 2. Создаем объект с обновленным текстом
        Comment updateRequest = new Comment();
        updateRequest.setText("Updated text");

        // 3. Вызываем обновление
        Optional<Comment> result = commentService.updateComment(existingComment.getId(), updateRequest);

        // 4. Проверяем результат
        assertTrue(result.isPresent());
        assertEquals("Updated text", result.get().getText());

        // 5. Проверяем, что в БД обновился именно этот комментарий
        Optional<Comment> updatedComment = commentRepository.findById(existingComment.getId());
        assertTrue(updatedComment.isPresent());
        assertEquals("Updated text", updatedComment.get().getText());
    }

    @Test
    void updateComment_WhenCommentDoesNotExist_ShouldReturnEmpty() {
        Comment updateRequest = new Comment();
        updateRequest.setText("Updated text");

        Optional<Comment> result = commentService.updateComment(999L, updateRequest);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteComment_WhenCommentExists_ShouldReturnTrue() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(testComment));
        doNothing().when(commentRepository).deleteById(TEST_COMMENT_ID);

        boolean result = commentService.deleteComment(TEST_COMMENT_ID);

        assertTrue(result);
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository).deleteById(TEST_COMMENT_ID);
    }

    @Test
    void deleteComment_WhenCommentDoesNotExist_ShouldReturnFalse() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.empty());

        boolean result = commentService.deleteComment(TEST_COMMENT_ID);

        assertFalse(result);
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository, never()).deleteById(anyLong());
    }
}