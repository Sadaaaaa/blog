package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("New Title");
        testPost.setText("Text");
        testPost.setTags(List.of("tag1"));
        testPost.setLikes(0);
    }

    @Test
    public void getAllPosts_ShouldReturnPageOfPosts() {
        List<Post> posts = List.of(testPost);
        when(postRepository.count(null)).thenReturn(1);
        when(postRepository.findAll(0, 10, null)).thenReturn(posts);
        when(commentRepository.getAllCommentsByPostIds(anyList())).thenReturn(new HashMap<>());

        Page<Post> result = postService.getAllPosts(0, 10, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(postRepository).findAll(0, 10, null);
    }

    @Test
    public void getPostById_ShouldReturnPostWithCommentsAndDetails() {
        byte[] imageData = new byte[]{1, 2, 3};
        List<String> tags = List.of("tag1", "tag2");
        List<Comment> comments = List.of(new Comment(1L, "Comment text"));

        Post post = new Post(1L, "Title", "Content", tags, 10, imageData);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostId(1L)).thenReturn(comments);

        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getText());
        assertEquals(10, result.getLikes());
        assertEquals(tags, result.getTags());
        assertArrayEquals(imageData, result.getImage());
        assertEquals(1, result.getComments().size());
        assertEquals("Comment text", result.getComments().getFirst().getText());

        verify(postRepository).findById(1L);
        verify(commentRepository).findByPostId(1L);
    }

    @Test
    public void addPost_ShouldCreateAndReturnNewPost() throws IOException {
        doNothing().when(postRepository).update(any(Post.class));
        when(postRepository.saveAndReturn(any(Post.class))).thenReturn(testPost);


        Post savedPost = postService.addPost("New Title", "Text", List.of("tag1"), null);
        assertNotNull(savedPost);
        assertEquals("New Title", savedPost.getTitle());
        assertEquals(1L, savedPost.getId().longValue());
        assertEquals(List.of("tag1"), savedPost.getTags());
        assertEquals(0, savedPost.getLikes());

        verify(postRepository).update(any(Post.class));
        verify(postRepository).saveAndReturn(any(Post.class));
    }

    @Test
    public void updatePost_WhenPostExists_ShouldUpdatePostDetails() throws IOException {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.updatePost(1L, "New Title", "New Text", List.of("tag1"), null);

        assertEquals("New Title", testPost.getTitle());
        verify(postRepository).update(testPost);
    }

    @Test
    public void deletePost_ShouldDeletePostAndItsComments() {
        when(commentRepository.deleteAllByPostId(1L)).thenReturn(2);

        postService.deletePost(1L);

        verify(postRepository).delete(1L);
        verify(commentRepository).deleteAllByPostId(1L);
    }

    @Test
    public void likePost_ShouldIncrementLikeCount() {
        testPost.setLikes(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.likePost(1L);

        assertEquals(6, testPost.getLikes());
        verify(postRepository).update(testPost);
    }

    @Test
    public void getImageByPostId_ShouldReturnPostImage() {
        byte[] imageData = new byte[]{1, 2, 3};
        when(postRepository.findImageByPostId(1L)).thenReturn(imageData);

        byte[] result = postService.getImageByPostId(1L);

        assertArrayEquals(imageData, result);
        verify(postRepository).findImageByPostId(1L);
    }
}

