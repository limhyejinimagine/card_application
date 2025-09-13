package com.imagine.card.card_application.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/*  카카오 > 토큰발급 */

@Service
@Slf4j
public class AuthService {

    private static String ACCESS_TOKEN;

    public void getAccessToken(String code) {
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "159776cea332c86d357453776dcdd5f0");
        params.add("redirect_uri", "");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(reqUrl, HttpMethod.POST, request, Map.class);
        ACCESS_TOKEN = response.getBody().get("access_token").toString();

        log.info("발급받은 토큰 access_token:{}", ACCESS_TOKEN);



    }

}
