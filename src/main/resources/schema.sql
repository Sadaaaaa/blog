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

-- Вставка комментариев для всех постов, без дублирования
-- Пост 1
INSERT INTO comments (post_id, text)
SELECT 1, 'Отличная статья, спасибо за информацию!'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE post_id = 1 AND text = 'Отличная статья, спасибо за информацию!');

INSERT INTO comments (post_id, text)
SELECT 1, 'Интересное мнение, но есть несколько неточностей!'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 1
                    AND text = 'Интересное мнение, но есть несколько неточностей!');

-- Пост 2
INSERT INTO comments (post_id, text)
SELECT 2, 'Очень полезно для новичков, спасибо!'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE post_id = 2 AND text = 'Очень полезно для новичков, спасибо!');

INSERT INTO comments (post_id, text)
SELECT 2, 'Я бы добавил ещё пару примеров, но в целом круто!'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 2
                    AND text = 'Я бы добавил ещё пару примеров, но в целом круто!');

-- Пост 3
INSERT INTO comments (post_id, text)
SELECT 3, 'Spring действительно мощный, но мне всё равно нравится Java EE.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 3
                    AND text = 'Spring действительно мощный, но мне всё равно нравится Java EE.');

-- Пост 4
INSERT INTO comments (post_id, text)
SELECT 4, 'PostgreSQL — отличная СУБД! Отличный выбор.'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE post_id = 4 AND text = 'PostgreSQL — отличная СУБД! Отличный выбор.');

-- Пост 5
INSERT INTO comments (post_id, text)
SELECT 5, 'Тестирование — ключевая часть разработки! Нужно больше материала.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 5
                    AND text = 'Тестирование — ключевая часть разработки! Нужно больше материала.');

-- Пост 6
INSERT INTO comments (post_id, text)
SELECT 6, 'Отличное руководство, но на практике встречается много нюансов.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 6
                    AND text = 'Отличное руководство, но на практике встречается много нюансов.');

-- Пост 7
INSERT INTO comments (post_id, text)
SELECT 7, 'Интересный взгляд на вещи, согласен с большинством пунктов.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 7
                    AND text = 'Интересный взгляд на вещи, согласен с большинством пунктов.');

-- Пост 8
INSERT INTO comments (post_id, text)
SELECT 8, 'Очень полезные советы, особенно по поводу кэширования.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 8
                    AND text = 'Очень полезные советы, особенно по поводу кэширования.');

-- Пост 9
INSERT INTO comments (post_id, text)
SELECT 9, 'Docker действительно изменил мой подход к разработке.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 9
                    AND text = 'Docker действительно изменил мой подход к разработке.');

-- Пост 10
INSERT INTO comments (post_id, text)
SELECT 10, 'Maven — отличная система, но бывает сложно разобраться в зависимостях.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 10
                    AND text = 'Maven — отличная система, но бывает сложно разобраться в зависимостях.');

-- Пост 11
INSERT INTO comments (post_id, text)
SELECT 11, 'Java 17 радует новыми улучшениями, особенно с точки зрения производительности.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 11
                    AND text = 'Java 17 радует новыми улучшениями, особенно с точки зрения производительности.');

-- Пост 12
INSERT INTO comments (post_id, text)
SELECT 12, 'Тестирование и CI/CD — ключевые компоненты современного процесса разработки.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 12
                    AND text = 'Тестирование и CI/CD — ключевые компоненты современного процесса разработки.');

-- Пост 13
INSERT INTO comments (post_id, text)
SELECT 13, 'Работа с базами данных в Spring Boot — это не так сложно, как может показаться.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 13
                    AND text = 'Работа с базами данных в Spring Boot — это не так сложно, как может показаться.');

-- Пост 14
INSERT INTO comments (post_id, text)
SELECT 14, 'Kotlin крутой язык, рекомендую попробовать!'
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE post_id = 14 AND text = 'Kotlin крутой язык, рекомендую попробовать!');

-- Пост 15
INSERT INTO comments (post_id, text)
SELECT 15, 'Очень интересно, согласен с большинством утверждений.'
WHERE NOT EXISTS (SELECT 1
                  FROM comments
                  WHERE post_id = 15
                    AND text = 'Очень интересно, согласен с большинством утверждений.');