-- Backs the new Parent entity (JOINED inheritance on User, same pattern as students/teachers).
CREATE TABLE IF NOT EXISTS parents (
    user_id BIGINT NOT NULL,
    CONSTRAINT parents_pkey PRIMARY KEY (user_id),
    CONSTRAINT fk_parents_user FOREIGN KEY (user_id) REFERENCES users (id)
);
