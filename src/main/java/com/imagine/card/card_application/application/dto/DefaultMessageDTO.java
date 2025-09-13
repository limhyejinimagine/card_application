package com.imagine.card.card_application.application.dto;

import lombok.Data;

/* 카카오 API  REQUEST 시 필수 DTO */
@Data
public class DefaultMessageDTO {
    private String objType;
    private String text;
    private String webUrl;
    private String mobileUrl;
    private String binTitle;
}
