package com.imagine.card.card_application.application.service.exception;

public class ValidationException extends BusinessException {
    public ValidationException(String code, String message) {
        super(code, message);
    }
}
