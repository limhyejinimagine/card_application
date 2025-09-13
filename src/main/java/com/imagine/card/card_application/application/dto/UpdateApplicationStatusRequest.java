package com.imagine.card.card_application.application.dto;

import com.imagine.card.card_application.domain.model.CardApplication;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 카드 상태 변경 신청 DTO */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationStatusRequest {
    private Long applicationId; // 변경할 신청 ID
    private CardApplication.ApplicationStatus status;      // APPROVED / REJECTED
    private String message;     // 승인/거절 사유
}
