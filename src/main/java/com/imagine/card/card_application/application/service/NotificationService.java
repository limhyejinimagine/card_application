package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.infrastructure.notification.AlimtalkSender;
import com.imagine.card.card_application.infrastructure.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* 카드 상태 전환 (승인/거절 등) 후속처리 > 알림톡/메일 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailSender emailSender;
    private final AlimtalkSender alimtalkSender;

    public void sendCardStatusChanged(Long appId, String status, String userEmail, String userPhone) {
        String msg = String.format("카드신청 %d번이 %s 되었습니다.", appId, status);
    }
}
