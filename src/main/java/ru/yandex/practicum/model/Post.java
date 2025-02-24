package ru.yandex.practicum.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likes;
    private List<Comment> comments;
    @Lob
    @Column(columnDefinition = "BYTEA")
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

