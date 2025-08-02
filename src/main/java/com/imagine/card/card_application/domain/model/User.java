package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id     // PK키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long user_id;

    private String name;

    @Column(unique = true)
    private String phone;

    private LocalDate birth_Date;

    @Column(unique = true)
    private String ci;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters 생략
}
