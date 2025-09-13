package com.imagine.card.card_application.infrastructure.notification;

/* 알림톡 인터페이스 */
public interface  AlimtalkSender {
    void send(String phoneNumber, String msg);
}
