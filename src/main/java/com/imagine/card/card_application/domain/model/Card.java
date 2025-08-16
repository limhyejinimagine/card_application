package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


/*  실제 발급 된 카드 정보   */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
@Table(name = "card")
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    // ERD: 1:1 with CardApplication (주인)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private CardApplication application;

    @Column(name = "card_number", unique = true, nullable = false, length = 20)
    private String cardNumber;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}