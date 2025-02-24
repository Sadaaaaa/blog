package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment comment1;
    private Comment comment2;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        comment1 = new Comment();
        comment1.setId(1L);
        comment1.setPostId(1L);
        comment1.setText("First comment");

        comment2 = new Comment();
        comment2.setId(2L);
        comment2.setPostId(1L);
        comment2.setText("Second comment");

        comments = Arrays.asList(comment1, comment2);
    }

    @Test
    void testGetCommentsByPostId() throws Exception {
        when(commentService.findByPostId(1L)).thenReturn(comments);

        mockMvc.perform(get("/comments/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[0].text").value("First comment"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].postId").value(1))
                .andExpect(jsonPath("$[1].text").value("Second comment"));

        verify(commentService).findByPostId(1L);
    }

    @Test
    void testGetCommentsByPostId_EmptyList() throws Exception {
        when(commentService.findByPostId(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/comments/post/99"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(commentService).findByPostId(99L);
    }

    @Test
    void testGetCommentById_Found() throws Exception {
        when(commentService.findById(1L)).thenReturn(Optional.of(comment1));

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.text").value("First comment"));

        verify(commentService).findById(1L);
    }

    @Test
    void testGetCommentById_NotFound() throws Exception {
        when(commentService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/comments/99"))
                .andExpect(status().isNotFound());

        verify(commentService).findById(99L);
    }

    @Test
    void testAddComment() throws Exception {
        doNothing().when(commentService).addComment(anyLong(), any(Comment.class));

        mockMvc.perform(post("/comments")
                        .param("postId", "1")
                        .param("text", "New comment"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/blog/posts/1"));

        verify(commentService).addComment(eq(1L), argThat(comment ->
                "New comment".equals(comment.getText())));
    }

    @Test
    void testEditComment_Success() throws Exception {
        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setText("Updated comment");

        when(commentService.updateComment(eq(1L), any(Comment.class)))
                .thenReturn(Optional.of(updatedComment));

        mockMvc.perform(post("/comments/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Updated comment\"}"))
                .andExpect(status().isOk());

        verify(commentService).updateComment(eq(1L), any(Comment.class));
    }

    @Test
    void testEditComment_NotFound() throws Exception {
        when(commentService.updateComment(eq(99L), any(Comment.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/comments/edit/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Updated comment\"}"))
                .andExpect(status().isNotFound());

        verify(commentService).updateComment(eq(99L), any(Comment.class));
    }

    @Test
    void testDeleteComment_Success() throws Exception {
        when(commentService.deleteComment(1L)).thenReturn(true);

        mockMvc.perform(post("/comments/delete/1"))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(1L);
    }

    @Test
    void testDeleteComment_NotFound() throws Exception {
        when(commentService.deleteComment(99L)).thenReturn(false);

        mockMvc.perform(post("/comments/delete/99"))
                .andExpect(status().isNotFound());

        verify(commentService).deleteComment(99L);
    }
}
