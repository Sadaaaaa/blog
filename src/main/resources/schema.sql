CREATE TABLE if not exists posts
(
    id        SERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    text      TEXT         NOT NULL,
    image     BYTEA,
    tags      TEXT[],
    likes     INT DEFAULT 0
);

CREATE TABLE if not exists comments
(
    id      SERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    text    TEXT   NOT NULL
);
