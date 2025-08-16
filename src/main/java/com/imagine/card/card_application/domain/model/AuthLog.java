package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


/*  인증관련로그  */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "auth_log", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class AuthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // nullable by schema

    private String ci; // nullable by schema

    @Column(length = 20)
    private String phone; // nullable by schema

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method", length = 50, nullable = false)
    private AuthMethod authMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AuthStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt; // default CURRENT_TIMESTAMP in DB

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @PrePersist
    void prePersist() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
    }

    public enum AuthMethod { SMS, APP, KAKAO, PASS, EMAIL }
    public enum AuthStatus { SUCCESS, FAILED }
}
