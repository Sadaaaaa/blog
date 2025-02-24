package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findAllTags() {
        String sql = "SELECT tags FROM posts";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}
