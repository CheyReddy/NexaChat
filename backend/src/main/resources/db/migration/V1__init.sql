CREATE TABLE users (
                       id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username      VARCHAR(50)  NOT NULL UNIQUE,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(100) NOT NULL,
                       display_name  VARCHAR(100) NOT NULL,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE conversations (
                               id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               type            VARCHAR(10)  NOT NULL CHECK (type IN ('DIRECT','GROUP')),
                               title           VARCHAR(100),
                               direct_key      VARCHAR(50)  UNIQUE,          -- e.g. '7_9' (smaller id first), NULL for groups
                               created_by      BIGINT       NOT NULL REFERENCES users(id),
                               created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               last_message_at TIMESTAMPTZ
);

CREATE TABLE conversation_participants (
                                           conversation_id      BIGINT      NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
                                           user_id              BIGINT      NOT NULL REFERENCES users(id),
                                           role                 VARCHAR(10) NOT NULL DEFAULT 'MEMBER',
                                           joined_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
                                           last_read_message_id BIGINT      NOT NULL DEFAULT 0,
                                           PRIMARY KEY (conversation_id, user_id)
);
CREATE INDEX idx_participants_user ON conversation_participants (user_id);

CREATE TABLE messages (
                          id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          conversation_id   BIGINT       NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
                          sender_id         BIGINT       NOT NULL REFERENCES users(id),
                          client_message_id UUID         NOT NULL,
                          content           TEXT         NOT NULL,
                          type              VARCHAR(10)  NOT NULL DEFAULT 'TEXT',
                          created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          edited_at         TIMESTAMPTZ,
                          deleted_at        TIMESTAMPTZ
);
-- THE most important index in the system (history paging by conversation)
CREATE INDEX idx_messages_conv_id_desc ON messages (conversation_id, id DESC);
-- Idempotency: the same client message can be stored only once per conversation
CREATE UNIQUE INDEX uq_messages_client_id ON messages (conversation_id, client_message_id);