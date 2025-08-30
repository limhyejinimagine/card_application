package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.application.dto.CardTypeResponse;
import com.imagine.card.card_application.application.dto.CreateCardTypeRequest;
import com.imagine.card.card_application.application.dto.UpdateCardTypeRequest;
import com.imagine.card.card_application.application.service.CardTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* 카드 상품관리 -- 관리자 전용 컨트롤러 */
@RestController
@RequestMapping("/card-types")
@RequiredArgsConstructor
public class CardTypeController {

    private final CardTypeService cardTypeService;

    /**
     * 카드 상품 등록
     */
    @PostMapping
    public ResponseEntity<CardTypeResponse> registCardType(@RequestBody @Valid CreateCardTypeRequest req) {
        var res = cardTypeService.registCardType(req);
        return ResponseEntity.status(201).body(res);
    }

    /**
     * 카드 상품 목록 조회
     */
    @GetMapping
    public List<CardTypeResponse> getCardTypeList() {
        return cardTypeService.getCardTypeList();
    }

    /**
     * 카드 상품 단건 조회
     */
    @GetMapping("/{id}")
    public CardTypeResponse getCardTypeOne(@PathVariable Long id) {
        return cardTypeService.getCardTypeOne(id);
    }

    /**
     * 카드 상품 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCardType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardTypeRequest req) {

        cardTypeService.updateCardType(id, req);
        return ResponseEntity.ok().build(); // 본문 없이 200 반환
    }

}
