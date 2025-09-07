package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
import com.imagine.card.card_application.application.service.CardApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/* 카드 신청 -- 사용자 전용 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class CardApplicationController {

    private final CardApplicationService service;

    /**
     * 카드 신청
     */
    @PostMapping
    public ResponseEntity<ApplyCardResponse> apply(@RequestBody @Valid ApplyCardRequest req) {
        var res = service.apply(req);
        return ResponseEntity.status(201).body(res);
    }

    /**
     * 신청 단건 조회 (신청한 카드의 진행상황을 확인)
     */


    /**
     * 신청 취소
     * */
    

}