package ru.yandex.practicum.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TagRepository tagRepository;

    @Test
    void testFindAllTags_ReturnsListOfTags() {
        List<String> expectedTags = Arrays.asList("Java", "Spring", "JUnit");
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
                .thenReturn(expectedTags);

        List<String> result = tagRepository.findAllTags();

        assertEquals(3, result.size());
        assertEquals(expectedTags, result);
        verify(jdbcTemplate).queryForList("SELECT tags FROM posts", String.class);
    }

    @Test
    void testFindAllTags_ReturnsEmptyList() {
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
                .thenReturn(Collections.emptyList());

        List<String> result = tagRepository.findAllTags();

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).queryForList("SELECT tags FROM posts", String.class);
    }

    @Test
    void testFindAllTags_ReturnsSingleTag() {
        List<String> expectedTags = Collections.singletonList("Java");
        when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
                .thenReturn(expectedTags);

        List<String> result = tagRepository.findAllTags();

        assertEquals(1, result.size());
        assertEquals("Java", result.getFirst());
        verify(jdbcTemplate).queryForList("SELECT tags FROM posts", String.class);
    }
}
