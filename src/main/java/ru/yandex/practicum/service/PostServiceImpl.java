package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Page<Post> getAllPosts(int page, int size, String tag) {
        int totalElements = postRepository.count(tag); // Получаем общее количество записей
        List<Post> posts = postRepository.findAll(page, size, tag);

        Map<Long, List<Comment>> comments = commentRepository.getAllCommentsByPostIds(posts.stream().map(Post::getId).toList());
        posts.forEach(post -> post.setComments(comments.getOrDefault(post.getId(), List.of()))); // сеттим комменты в посты


        return new PageImpl<>(posts, PageRequest.of(page, size), totalElements);
    }

    @Override
    public Post getPostById(Long id) {

        Post post = postRepository.findById(id).orElseThrow();

        List<Comment> comments = commentRepository.findByPostId(id);
        post.setComments(comments);

        return post;
    }

    @Override
    public Post addPost(String title, String text, List<String> tagList, MultipartFile image) throws IOException {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setTags(tagList);
        post.setLikes(0);

        if (image != null && !image.isEmpty()) {
            post.setImage(image.getBytes());
        }

        postRepository.update(post);

        return postRepository.saveAndReturn(post);
    }

    @Override
    public void updatePost(Long id, String title, String text, List<String> tagList, MultipartFile image) throws IOException {
        Post post = getPostById(id);

        if (!tagList.isEmpty()) {
            List<String> currentTags = this.getPostById(id).getTags();
            Set<String> newTags = new HashSet<>(tagList);
            newTags.addAll(currentTags);
            post.setTags(new ArrayList<>(newTags));
        }

        if (image != null && !image.isEmpty()) {
            post.setImage(image.getBytes());
        }

        post.setTitle(title);
        post.setText(text);
        postRepository.update(post);
    }

    @Transactional
    @Override
    public void deletePost(Long id) {
        postRepository.delete(id);
        int amountDeletedComments = commentRepository.deleteAllByPostId(id);
        log.info("Удалено комментариев: {}", amountDeletedComments);
    }

    @Override
    public void likePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            p.setLikes(p.getLikes() + 1);
            postRepository.update(p);
        });
    }

    @Override
    public byte[] getImageByPostId(Long id) {
        return postRepository.findImageByPostId(id);
    }
}
