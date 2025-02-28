package ru.yandex.practicum.repository;

import lombok.NonNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    // Получение списка комментариев к посту
    @Query("""
            SELECT * FROM comments WHERE post_id = :postId ORDER BY id ASC
            """)
    List<Comment> findByPostId(Long postId);

    // Поиск комментария по ID
    @NonNull
    @Query("""
            SELECT * FROM comments WHERE id = :id
            """)
    Optional<Comment> findById(@NonNull Long id);

    // Сохранение нового комментария
    @NonNull
    <S extends Comment> S save(@NonNull S Comment);

    // Обновление комментария
    @Modifying
    @Query("""
            UPDATE comments SET text = :#{#comment.text} WHERE id = :#{#comment.id}
            """)
    int update(@Param("comment") Comment comment);

    // Удаление комментария по id
    void deleteById(@NonNull Long id);

    // Удаление комментариев по id поста
    @Modifying
    @Transactional
    @Query("DELETE FROM comments WHERE post_id = :postId")
    int deleteAllByPostId(Long postId);


    // Получение всех комментариев по списку идентификаторов постов
    @Query("""
            SELECT * FROM comments WHERE post_id IN (:postIds) ORDER BY id ASC
            """)
    List<Comment> getAllCommentsByPostIds(List<Long> postIds);
}

