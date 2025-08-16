-- USERS
CREATE TABLE IF NOT EXISTS user (
                                    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    name VARCHAR(255),
    phone VARCHAR(255) UNIQUE,
    birth_date DATE,
    ci VARCHAR(255) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- CARD TYPE
CREATE TABLE IF NOT EXISTS card_type (
                                         card_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- CARD APPLICATION
CREATE TABLE IF NOT EXISTS card_application (
                                                application_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                user_id BIGINT NOT NULL,
                                                card_type_id BIGINT NOT NULL,
                                                status VARCHAR(30) NOT NULL,
    requested_at DATETIME NOT NULL,
    approved_at DATETIME DEFAULT NULL,
    issued_at DATETIME DEFAULT NULL,
    rejection_reason TEXT,
    CONSTRAINT uk_user_cardtype UNIQUE (user_id, card_type_id),
    CONSTRAINT fk_app_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_app_card_type FOREIGN KEY (card_type_id) REFERENCES card_type(card_type_id)
    );

-- CARD
CREATE TABLE IF NOT EXISTS card (
                                    card_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    application_id BIGINT NOT NULL,
                                    card_number VARCHAR(20) UNIQUE NOT NULL,
    issued_date DATETIME NOT NULL,
    expiry_date DATETIME NOT NULL,
    CONSTRAINT fk_card_app FOREIGN KEY (application_id) REFERENCES card_application(application_id)
    );

-- APPLICATION STATUS HISTORY
CREATE TABLE IF NOT EXISTS application_status_history (
                                                          history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                          application_id BIGINT NOT NULL,
                                                          status VARCHAR(30) NOT NULL,
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    message TEXT,
    KEY idx_hist_app (application_id),
    CONSTRAINT fk_hist_app FOREIGN KEY (application_id) REFERENCES card_application(application_id)
    );

-- AUTH LOG
CREATE TABLE IF NOT EXISTS auth_log (
                                        auth_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        user_id BIGINT NULL,
                                        ci VARCHAR(255) NULL,
    phone VARCHAR(20) NULL,
    auth_method VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    failure_reason TEXT,
    KEY idx_auth_user (user_id),
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES user(user_id)
    );
