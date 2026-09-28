CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS target (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    category VARCHAR(255),
    daily_minutes INT,
    completed_minutes INT,
    completed_today BIT,
    progress_date DATE,
    user_id BIGINT,
    PRIMARY KEY (id),
    KEY idx_target_user_id (user_id),
    CONSTRAINT fk_target_user
        FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS resource (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    type VARCHAR(255),
    url VARCHAR(255),
    content VARCHAR(255),
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    content_type VARCHAR(255),
    target_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_resource_target_id (target_id),
    CONSTRAINT fk_resource_target
        FOREIGN KEY (target_id) REFERENCES target (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS learning_session (
    id BIGINT NOT NULL AUTO_INCREMENT,
    duration_minutes INT,
    started_at DATETIME(6),
    ended_at DATETIME(6),
    target_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_learning_session_target_id (target_id),
    CONSTRAINT fk_learning_session_target
        FOREIGN KEY (target_id) REFERENCES target (id)
) ENGINE=InnoDB;
