package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.model.Post;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void saveAndReturn_shouldSavePost() {
        Post post = new Post(null, "Title", "Text", List.of("java", "spring"), 10, null);
        Post savedPost = postRepository.save(post);

        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("Title");
    }

    @Test
    void findById_shouldReturnPost_whenExists() {
        Post post = new Post(null, "Title", "Text", List.of("java"), 5, null);
        Post savedPost = postRepository.save(post);

        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Title");
    }

    @Test
    void testFindAllTags_ReturnsListOfTags() throws IOException {
        Post post = new Post(null, "Title", "Text", List.of("Java", "Spring", "JUnit"), 5, null);
        postRepository.save(post);

        List<String> result = postRepository.findAllTags();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        assertEquals("Java,Spring,JUnit", result.getFirst());
    }

    @Test
    void testFindAllTags_ReturnsEmptyList() {
        List<String> result = postRepository.findAllTags();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllTags_ReturnsSingleTag() {
        Post post = new Post(null, "Title", "Text", List.of("Java"), 5, null);
        postRepository.save(post);

        List<String> result = postRepository.findAllTags();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        assertEquals("Java", result.getFirst());
    }

}


