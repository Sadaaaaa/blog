package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.NativeQueryRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceImplTest {

    @Autowired
    private PostServiceImpl postService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private NativeQueryRepository nativeQueryRepository;

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
        when(commentRepository.getAllCommentsByPostIds(anyList())).thenReturn(new ArrayList<>());

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
        when(postRepository.save(any(Post.class))).thenReturn(testPost);


        Post savedPost = postService.addPost("New Title", "Text", List.of("tag1"), null);
        assertNotNull(savedPost);
        assertEquals("New Title", savedPost.getTitle());
        assertEquals(1L, savedPost.getId().longValue());
        assertEquals(List.of("tag1"), savedPost.getTags());
        assertEquals(0, savedPost.getLikes());
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

        verify(postRepository).deleteById(1L);
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
        when(nativeQueryRepository.findImageByPostId(1L)).thenReturn(imageData);

        byte[] result = postService.getImageByPostId(1L);

        assertArrayEquals(imageData, result);
        verify(nativeQueryRepository).findImageByPostId(1L);
    }

    @Test
    void getAllTags_WhenTagsExist_ShouldReturnUniqueTagsList() {
        List<String> rawTags = Arrays.asList(
                "java,spring,hibernate",
                "spring,junit,java",
                "docker,java"
        );
        when(postRepository.findAllTags()).thenReturn(rawTags);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring", "hibernate", "junit", "docker")));
        verify(postRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenEmptyTags_ShouldReturnEmptyList() {
        List<String> emptyTags = Arrays.asList("");
        when(postRepository.findAllTags()).thenReturn(emptyTags);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenNullTags_ShouldReturnEmptyList() {
        List<String> tagsWithNull = Arrays.asList(null, "java,spring", null);
        when(postRepository.findAllTags()).thenReturn(tagsWithNull);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring")));
        verify(postRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenMixedTags_ShouldReturnUniqueNonEmptyTags() {
        List<String> mixedTags = Arrays.asList(
                "java,spring",
                "",
                null,
                "spring,java,junit",
                "  "
        );
        when(postRepository.findAllTags()).thenReturn(mixedTags);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring", "junit")));
        verify(postRepository).findAllTags();
    }
}

