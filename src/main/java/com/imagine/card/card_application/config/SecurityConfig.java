package com.imagine.card.card_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").permitAll() // /users 경로는 인증 없이 허용
                        .anyRequest().authenticated()         // 그 외는 인증 필요
                )
                .formLogin(Customizer.withDefaults()); // 기본 로그인 폼 사용 (비활성화하고 싶으면 .disable())

        return http.build();
    }
}
