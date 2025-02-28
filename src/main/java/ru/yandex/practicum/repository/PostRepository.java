package ru.yandex.practicum.repository;

import lombok.NonNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    // Получение списка постов с пагинацией
    @Query("""
            SELECT *
            FROM posts
            WHERE (:tag IS NULL OR array_to_string(tags, ',') LIKE CONCAT('%', :tag, '%'))
            ORDER BY id DESC
            LIMIT :size OFFSET :offset
            """)
    List<Post> findAll(int offset, int size, String tag);


    @Query("""
        SELECT COUNT(*) FROM posts
        WHERE (:tag IS NULL OR array_to_string(tags, ',') LIKE CONCAT('%', :tag, '%'))
        """)
    Integer count(String tag);

    // Поиск поста по ID
    @NonNull
    Optional<Post> findById(@NonNull Long id);

    // Сохранение нового поста
    <S extends Post> S save(S post);

    // Обновление поста
    @Modifying
    @Query("""
            UPDATE posts
            SET title = :#{#post.title},
            text = :#{#post.text},
            tags = :#{T(String).join(',', #post.tags)},
            likes = :#{#post.likes},
            image = :#{#post.image}
            WHERE id = :#{#post.id}
            """)
    void update(Post post);

    // Удаление поста
    void deleteById(@NonNull Long id);

    @Query("SELECT array_to_string(p.tags, ',') FROM posts p WHERE p.tags IS NOT NULL")
    List<String> findAllTags();
}
