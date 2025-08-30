package com.imagine.card.card_application.domain.model;

import com.imagine.card.card_application.application.dto.CardTypeResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.ResponseEntity;

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

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ERD: CardType 1:N CardApplication
    @OneToMany(mappedBy = "cardType", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardApplication> applications = new ArrayList<>();

    // 카드 활성화 여부 디폴트값 true 로 세팅 (특정 카드 상품을 비활성화 따로 뒀음)
    @PrePersist
    public void prePersist() {
        if(isActive == null) isActive = true;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        if(isActive == null) isActive = true;
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

}