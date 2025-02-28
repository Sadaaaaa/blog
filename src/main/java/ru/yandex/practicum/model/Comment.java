package ru.yandex.practicum.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("comments")
public class Comment {
    @Id
    private Long id;
    private Long postId;
    private String text;

    public Comment() {
    }

    public Comment(Long id, Long postId, String text) {
        this.id = id;
        this.postId = postId;
        this.text = text;
    }

    public Comment(Long postId, String text) {
        this.postId = postId;
        this.text = text;
    }
}
