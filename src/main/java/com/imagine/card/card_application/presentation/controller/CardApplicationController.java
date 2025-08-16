package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
import com.imagine.card.card_application.application.service.CardApplicationService;
import com.imagine.card.card_application.domain.model.CardApplication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class CardApplicationController {
    private final CardApplicationService service;

    @PostMapping
    public ResponseEntity<ApplyCardResponse> apply(@RequestBody @Valid ApplyCardRequest req) {
        var res = service.apply(req);
        return ResponseEntity.status(201).body(res);
    }
}