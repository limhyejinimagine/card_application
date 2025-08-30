package com.imagine.card.card_application.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCardTypeRequest(
        @Size(min=2, max = 30, message= "{cardType.name.size}")
        String name,

        @Size(max = 255, message="{cardType.description.size}")
        String description,

        Boolean isActive
) {

}
