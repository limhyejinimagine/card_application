package com.imagine.card.card_application.application.service.validator;

import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder    // cardTypeService 는 user 필드가 필요 없어서 빌더패턴 씀
public class ValidationResult {
    User user;
    CardType cardType;
}
