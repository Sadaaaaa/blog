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
import ru.yandex.practicum.repository.NativeQueryRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NativeQueryRepository nativeQueryRepository;

    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository, NativeQueryRepository nativeQueryRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.nativeQueryRepository = nativeQueryRepository;
    }

    @Override
    public Page<Post> getAllPosts(int page, int size, String tag) {
        int totalElements = postRepository.count(tag); // Получаем общее количество записей
        List<Post> posts = postRepository.findAll(page * size, size, tag);

        if (posts.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), totalElements);
        }

        List<Comment> comments = commentRepository.getAllCommentsByPostIds(posts.stream().map(Post::getId).toList());
        Map<Long, List<Comment>> commentsMap = comments.stream().collect(Collectors.groupingBy(Comment::getPostId));
        posts.forEach(post -> post.setComments(commentsMap.getOrDefault(post.getId(), List.of()))); // сеттим комменты в посты

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

        return postRepository.save(post);
    }

    @Override
    public void updatePost(Long id, String title, String text, List<String> tagList, MultipartFile image) throws IOException {
        Post post = getPostById(id);

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
        postRepository.deleteById(id);
        int amountDeletedComments = commentRepository.deleteAllByPostId(id);
        log.info("Удалено комментариев: {}", amountDeletedComments);
    }

    @Transactional
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
        return nativeQueryRepository.findImageByPostId(id);
    }

    @Override
    public List<String> getAllTags() {
        List<String> allTags = postRepository.findAllTags();

        Set<String> uniqueTags = new HashSet<>();
        for (String tags : allTags) {
            if (tags != null && !tags.isBlank()) {
                uniqueTags.addAll(Arrays.asList(tags.split(",")));
            }
        }

        return new ArrayList<>(uniqueTags);
    }
}
