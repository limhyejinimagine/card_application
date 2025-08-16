package com.imagine.card.card_application.application.service.exception;

public class NotFoundException extends BusinessException{
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
