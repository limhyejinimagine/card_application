package com.imagine.card.card_application.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/* 알림톡 구현체 > 카카오톡 알림 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AlimtalkSenderImpl implements AlimtalkSender {

    @Override
    public void send(String phoneNumber, String msg) {

    }
}
