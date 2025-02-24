package ru.yandex.practicum.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.service.CommentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Получение всех комментариев к посту
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        System.out.println("Получение комментариев для postId: " + postId);
        List<Comment> comments = commentService.findByPostId(postId);
        System.out.println("Найдено комментариев: " + comments.size());
        return ResponseEntity.ok(comments);
    }

    // Получение конкретного комментария по его ID
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Optional<Comment> comment = commentService.findById(commentId);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Добавление нового комментария
    @PostMapping
    public ResponseEntity<String> addComment(@RequestParam("postId") Long postId,
                                             @RequestParam("text") String text) {
        Comment comment = new Comment();
        comment.setText(text);
        commentService.addComment(postId, comment);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/blog/posts/" + postId)
                .build();
    }

    // Обновление существующего комментария
    @PostMapping("/edit/{commentId}")
    public ResponseEntity<String> editComment(@PathVariable Long commentId, @RequestBody Comment comment) {
        Optional<Comment> updated = commentService.updateComment(commentId, comment);
        return updated.isPresent()
                ? ResponseEntity.ok("Комментарий обновлён")
                : ResponseEntity.notFound().build();
    }

    // Удаление комментария
    @PostMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId)
                ? ResponseEntity.ok("Комментарий удалён")
                : ResponseEntity.notFound().build();
    }
}

