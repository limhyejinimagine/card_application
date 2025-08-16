package com.imagine.card.card_application.application.dto;

import jakarta.validation.constraints.*;


public record ApplyCardRequest(
        @NotNull Long userId,
        @NotNull Long cardTypeId
) {}