package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> findByPostId(Long postId);
    Optional<Comment> findById(Long commentId);
    void addComment(Long postId, Comment comment);
    Optional<Comment> updateComment(Long commentId, Comment comment);

    boolean deleteComment(Long id);
}
