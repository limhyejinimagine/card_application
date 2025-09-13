package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="outbox_event")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CardApplication , User 등 도메인 구분
    private String aggregateType;

    // 대상 PK
    private Long aggregateId;

    // Kafka 토픽
    private String type;

    @Column(columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    // kafka 발행시 상태변경 : NEW -> SENT
    public void setSent() {
        this.status = OutboxStatus.SENT;
    }

}
