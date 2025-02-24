CREATE TABLE if not exists posts
(
    id        SERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    text      TEXT         NOT NULL,
    image     BYTEA,
    tags      TEXT,
    likes     INT DEFAULT 0
);

CREATE TABLE if not exists comments
(
    id      SERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    text    TEXT   NOT NULL
);


-- Вставка 15 случайных постов, без дублирования
INSERT INTO posts (title, text, tags)
SELECT 'Пост №1', 'Это первый пост с случайным текстом.', 'tag1'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №1');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №2', 'Этот пост о том, как важно учиться программированию.', 'tag2'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №2');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №3', 'Сегодня я хочу рассказать вам о Spring Framework.', 'tag3'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №3');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №4', 'Не так давно я научился работать с PostgreSQL.', 'tag1'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №4');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №5', 'Тема сегодняшнего поста — тестирование приложений.', 'tag2'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №5');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №6', 'Как настроить систему для разработки на Java.', 'tag3'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №6');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №7', 'Вот такие интересные штуки я недавно узнал.', 'tag1'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №7');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №8', 'Лучшие практики работы с REST API в Spring Boot.', 'tag2'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №8');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №9', 'Почему стоит использовать Docker в разработке?', 'tag3'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №9');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №10', 'Как я автоматизировал свои процессы с помощью Maven.', 'tag1'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №10');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №11', 'Что нового в Java 17? Все, что нужно знать!', 'tag2'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №11');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №12', 'Тестирование и CI/CD в современной разработке.', 'tag3'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №12');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №13', 'Работа с базами данных в Spring Boot.', 'tag1'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №13');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №14', 'Почему я перешел на Kotlin для разработки.', 'tag2'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №14');

INSERT INTO posts (title, text, tags)
SELECT 'Пост №15', 'Мои мысли о будущем программирования.', 'tag3'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Пост №15');

INSERT INTO comments (post_id, text)
SELECT p.id, comment_texts.text  -- Явно указываем источник text
FROM posts p
         LEFT JOIN comments c ON p.id = c.post_id
         CROSS JOIN (VALUES
                         ('Отличная статья, спасибо за информацию!'),
                         ('Интересное мнение, но есть несколько неточностей!'),
                         ('Очень полезно для новичков, спасибо!'),
                         ('Spring действительно мощный, но мне всё равно нравится Java EE.'),
                         ('PostgreSQL — отличная СУБД! Отличный выбор.')
) AS comment_texts(text)
WHERE c.post_id IS NULL;
