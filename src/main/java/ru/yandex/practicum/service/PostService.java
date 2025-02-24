package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Post;

import java.io.IOException;
import java.util.List;

public interface PostService {
    Page<Post> getAllPosts(int page, int size, String tag);

    Post getPostById(Long id);

    Post addPost(String title, String text, List<String> tagList, MultipartFile image) throws IOException;

    void updatePost(Long id, String title, String text, List<String> tagList, MultipartFile image) throws IOException;

    void deletePost(Long id);

    void likePost(Long id);

    byte[] getImageByPostId(Long id);
}
