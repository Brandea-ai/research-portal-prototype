-- ============================================================
-- V5: Watchlist-Tabelle fuer Analysten-Wertschriften-Beobachtung
-- ============================================================

CREATE TABLE watchlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    security_id BIGINT NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(500),
    alert_on_new_report BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_watchlist_security FOREIGN KEY (security_id) REFERENCES security(id),
    CONSTRAINT uk_watchlist_user_security UNIQUE (user_id, security_id)
);
