package ru.yandex.practicum.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private Model model;

    @InjectMocks
    private PostController postController;

    private Gson gson;
    private Post post1;
    private Post post2;
    private List<Post> posts;
    private byte[] testImage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        gson = new Gson();

        post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Test Post 1");
        post1.setText("Test content 1");
        post1.setTags(Arrays.asList("java", "spring"));

        post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Test Post 2");
        post2.setText("Test content 2");
        post2.setTags(Arrays.asList("junit", "mockito"));

        posts = Arrays.asList(post1, post2);

        // кладем байты в переменную testImage
        testImage = "Test Image Data".getBytes();
        post1.setImage(testImage);
    }

    @Test
    void testListPosts() throws Exception {
        Page<Post> page = new PageImpl<>(posts);
        when(postService.getAllPosts(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "5")
                        .param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("size"))
                .andExpect(model().attributeExists("selectedTag"));

        verify(postService).getAllPosts(0, 5, "java");
    }

    @Test
    void testGetPostImage_Success() throws Exception {
        when(postService.getImageByPostId(1L)).thenReturn(testImage);

        mockMvc.perform(get("/posts/1/image"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(content().bytes(testImage));

        verify(postService).getImageByPostId(1L);
    }

    @Test
    void testGetPostImage_NotFound() throws Exception {
        when(postService.getImageByPostId(99L)).thenReturn(null);

        mockMvc.perform(get("/posts/99/image"))
                .andExpect(status().isNotFound());

        verify(postService).getImageByPostId(99L);
    }

    @Test
    void testViewPost_Success() throws Exception {
        when(postService.getPostById(1L)).thenReturn(post1);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("base64Image"));

        verify(postService).getPostById(1L);
    }

    @Test
    void testViewPost_NotFound() throws Exception {
        when(postService.getPostById(99L)).thenReturn(null);

        mockMvc.perform(get("/posts/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts?error=not_found"));

        verify(postService).getPostById(99L);
    }

    @Test
    void testAddPost() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        List<String> tags = Arrays.asList("java", "spring");
        when(postService.addPost(anyString(), anyString(), anyList(), any())).thenReturn(post1);

        mockMvc.perform(multipart("/posts/add")
                        .file(imageFile)
                        .param("title", "New Post")
                        .param("text", "New post content")
                        .param("tags", "java,spring"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).addPost(eq("New Post"), eq("New post content"), argThat(list ->
                list.size() == 2 && list.contains("java") && list.contains("spring")), any());
    }

    @Test
    void testEditPost() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "updated image content".getBytes()
        );

        String tagsJson = gson.toJson(Arrays.asList("updated", "tags"));

        mockMvc.perform(multipart("/posts/edit")
                        .file(imageFile)
                        .param("id", "1")
                        .param("title", "Updated Title")
                        .param("text", "Updated content")
                        .param("tags", tagsJson))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        verify(postService).updatePost(eq(1L), eq("Updated Title"), eq("Updated content"),
                argThat(list -> list.size() == 2 && list.contains("updated") && list.contains("tags")), any());
    }

    @Test
    void testEditPost_MissingId() throws Exception {
        mockMvc.perform(multipart("/posts/edit")
                        .param("title", "Updated Title")
                        .param("text", "Updated content"))
                .andExpect(status().isBadRequest());

        verify(postService, never()).updatePost(any(), any(), any(), any(), any());
    }

    @Test
    void testDeletePost() throws Exception {
        mockMvc.perform(post("/posts/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts"));

        verify(postService).deletePost(1L);
    }

    @Test
    void testLikePost() throws Exception {
        mockMvc.perform(post("/posts/like/1"))
                .andExpect(status().isOk());

        verify(postService).likePost(1L);
    }
}
