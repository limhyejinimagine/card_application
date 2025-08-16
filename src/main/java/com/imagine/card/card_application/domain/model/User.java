package com.imagine.card.card_application.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*      사용자 기본정보    */

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "user",
        indexes = {
                @Index(name = "idx_user_phone", columnList = "phone"),
                @Index(name = "idx_user_ci", columnList = "ci")
        })
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(unique = true)
    private String ci;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ERD: User 1:N CardApplication
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardApplication> applications = new ArrayList<>();
}