package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.event.CardIssuedEvent;
import com.imagine.card.card_application.event.ValidationFailedEvent;

public interface CardApplicationEventPublisher {
    void publishValidationFailed(ValidationFailedEvent event);
    void publishCardRequested(CardApplicationRequestedEvent event);
    void publishCardIssued(CardIssuedEvent event);
    void publishStatusChanged(CardApplicationStatusChangedEvent event);
}
