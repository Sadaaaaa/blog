package ru.yandex.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NativeQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public NativeQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public byte[] findImageByPostId(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT image FROM posts WHERE id = ?",
                new Object[]{id},
                byte[].class
        );
    }
}
