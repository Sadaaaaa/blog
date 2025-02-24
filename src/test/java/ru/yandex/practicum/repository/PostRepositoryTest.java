package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.model.Post;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class PostRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost:5432/blogDB", "postgres", "postgres"
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        postRepository = new PostRepository(jdbcTemplate);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS posts (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "text TEXT, " +
                "tags VARCHAR(255), " +
                "likes INT, " +
                "image BYTEA)");

        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void saveAndReturn_shouldSavePost() {
        Post post = new Post(null, "Title", "Text", List.of("java", "spring"), 10, null);
        Post savedPost = postRepository.saveAndReturn(post);

        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("Title");
    }

    @Test
    void findById_shouldReturnPost_whenExists() {
        Post post = new Post(null, "Title", "Text", List.of("java"), 5, null);
        Post savedPost = postRepository.saveAndReturn(post);

        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Title");
    }
}


