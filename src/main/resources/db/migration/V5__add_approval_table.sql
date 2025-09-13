CREATE TABLE approval (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      application_id BIGINT NOT NULL,   -- 어떤 신청건인지 (FK)
      user_id BIGINT NOT NULL,          -- 심사 대상자
      result VARCHAR(20) NOT NULL,      -- APPROVED / REJECTED
      reason VARCHAR(255),              -- 거절/승인 사유
      credit_score INT,                 -- (Mock) 신용 점수
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
