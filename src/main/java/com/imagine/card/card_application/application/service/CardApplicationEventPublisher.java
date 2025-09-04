package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.event.*;

public interface CardApplicationEventPublisher {
    void publishValidationFailed(ValidationFailedEvent event);
    void publishCardRequested(CardApplicationRequestedEvent event);
    void publishCardIssued(CardIssuedEvent event);
    void publishStatusChanged(CardApplicationStatusChangedEvent event);
}
