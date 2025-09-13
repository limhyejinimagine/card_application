package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

/* 카드 심사 테이블
*   카드 신청(card_application) : 심사(approval) = 1 : N
* */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="approval")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청 1 : 심사 N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id" , nullable=false) // fk
    private CardApplication application;

    // user_id 도 FK로 관리가능 (심사 대상자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id" , nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private ApprovalResult result;

    private String reason;

    private int credit_score;

    private LocalDateTime created_at;

    public Approval(User user, ApprovalResult approvalResult) {
        this.user = user;
        this.result = approvalResult;
    }

    public Approval(CardApplication ca, User user, ApprovalResult result, int i, LocalDateTime createdAt) {
        this.application = ca;
        this.user = user;
        this.result = result;
        this.credit_score = i;
        this.created_at = createdAt;
    }


    @PrePersist
    public void prePersist(){
        this.created_at = LocalDateTime.now();
    }
    
    public enum ApprovalResult {
        APPROVED("심사승인"),
        REJECTED("심사실패");
        
        private final String descrition;

        ApprovalResult(String descrition) {
            this.descrition = descrition;
        }

        public String getDescrition() {
            return descrition;
        }
    }
    
}
