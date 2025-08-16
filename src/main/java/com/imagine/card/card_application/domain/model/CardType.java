package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*      발급 가능한 카드 종류    */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card_type")
public class CardType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_type_id")
    private Long id;

    private String name;
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ERD: CardType 1:N CardApplication
    @OneToMany(mappedBy = "cardType", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardApplication> applications = new ArrayList<>();
}