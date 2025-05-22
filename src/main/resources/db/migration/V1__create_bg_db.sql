CREATE TABLE board_games (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    min_players INTEGER,
    max_players INTEGER,
    play_time_minutes INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);