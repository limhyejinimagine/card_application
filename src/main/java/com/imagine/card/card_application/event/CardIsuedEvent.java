package com.imagine.card.card_application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*  발급완료용 DTO   */
@Data@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardIsuedEvent {
    private Long appId;
    private Long userId;
    private String cardNumber;
    private LocalDateTime issuedDate;
}
