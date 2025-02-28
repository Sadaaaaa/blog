package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table("posts")
public class Post {
    @Id
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likes;

    @Transient
    private List<Comment> comments;
    @Lob
    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;

    public Post(Long id, String title, String text, List<String> tags, int likes, byte[] image) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likes = likes;
        this.image = image;
    }
}

