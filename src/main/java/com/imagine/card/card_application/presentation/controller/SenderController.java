package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.infrastructure.notification.AlimtalkSender;
import com.imagine.card.card_application.infrastructure.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/* 외부 메일, 알림 테스트 컨트롤러 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sender")
public class SenderController {

    private final EmailSender emailSender;
    private final AlimtalkSender alimtalkSender;

    @GetMapping("/email")
    @ResponseBody
    public void senderEmail() {
        String to = "";
        String subject = "";
        String body = "";
        emailSender.send(to,subject,body);
    }
}
