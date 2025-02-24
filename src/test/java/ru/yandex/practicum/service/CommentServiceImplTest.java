package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment testComment;
    private final Long TEST_POST_ID = 1L;
    private final Long TEST_COMMENT_ID = 1L;

    @BeforeEach
    void setUp() {
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
        Comment updateRequest = new Comment();
        updateRequest.setText("Updated text");

        Comment existingComment = new Comment();
        existingComment.setId(TEST_COMMENT_ID);
        existingComment.setText("Original text");

        Comment updatedComment = new Comment();
        updatedComment.setId(TEST_COMMENT_ID);
        updatedComment.setText("Updated text");

        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(existingComment));
        when(commentRepository.update(any(Comment.class))).thenReturn(updatedComment);



        Optional<Comment> result = commentService.updateComment(TEST_COMMENT_ID, updateRequest);

        assertTrue(result.isPresent());
        assertEquals("Updated text", result.get().getText());
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository).update(any(Comment.class));
    }

    @Test
    void updateComment_WhenCommentDoesNotExist_ShouldReturnEmpty() {
        Comment updateRequest = new Comment();
        updateRequest.setText("Updated text");

        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.empty());


        Optional<Comment> result = commentService.updateComment(TEST_COMMENT_ID, updateRequest);

        assertFalse(result.isPresent());
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository, never()).update(any(Comment.class));
    }

    @Test
    void deleteComment_WhenCommentExists_ShouldReturnTrue() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(testComment));
        doNothing().when(commentRepository).delete(TEST_COMMENT_ID);

        boolean result = commentService.deleteComment(TEST_COMMENT_ID);

        assertTrue(result);
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository).delete(TEST_COMMENT_ID);
    }

    @Test
    void deleteComment_WhenCommentDoesNotExist_ShouldReturnFalse() {
        when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.empty());

        boolean result = commentService.deleteComment(TEST_COMMENT_ID);

        assertFalse(result);
        verify(commentRepository).findById(TEST_COMMENT_ID);
        verify(commentRepository, never()).delete(anyLong());
    }
}