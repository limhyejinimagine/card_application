package com.imagine.card.card_application.application.service.exception;

// 커스텀 예외
public class BusinessException extends RuntimeException{

    private final String code;

    public BusinessException(String code,String message) {
        super(message);
        this.code = code;
    }

    public String code() {return code;}
}
