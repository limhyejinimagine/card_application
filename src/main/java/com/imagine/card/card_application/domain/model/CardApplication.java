package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*      카드 신청 정보    */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card_application",
        uniqueConstraints = @UniqueConstraint(name="uk_user_cardtype", columnNames = {"user_id","card_type_id"}))
public class CardApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_type_id", nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ApplicationStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ERD: CardApplication 1:N ApplicationStatusHistory
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationStatusHistory> histories = new ArrayList<>();

    // ERD: CardApplication 1:1 Card (양방향의 주인은 Card)
    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY, optional = true)
    private Card card;

    @PrePersist
    void prePersist() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (status == null) status = ApplicationStatus.REQUESTED;
    }

    public enum ApplicationStatus { REQUESTED, FAILED, APPROVED, ISSUED, REJECTED }
}
