package com.imagine.card.card_application.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/* 외부 이메일 발송 구현체 > gmail */
@Service
@Slf4j
@RequiredArgsConstructor
public class SmtpEmailSenderImpl implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void send (String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("1m4g1ne2018@gmail.com");

        mailSender.send(message);
        log.info("Email sent successfully . .  .");
    }

}
