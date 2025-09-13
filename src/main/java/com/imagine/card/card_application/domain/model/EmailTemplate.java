package com.imagine.card.card_application.domain.model;

import lombok.Getter;

/* 이메일 발송 템플릿 정의 */
public enum EmailTemplate {
    APPROVED("카드 승인 안내", "신청번호 %s 건이 승인되었습니다."),
    REJECTED("카드 거절 안내", "신청번호 %s 건이 거절되었습니다. 사유: %s"),
    ISSUED("카드 발급 완료", "신청번호 %s 건의 카드가 발급되었습니다.");

    @Getter
    private final String subject;
    private final String body;

    EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String formatBody(Object... args) {
        return String.format(body,args);
    }

}
