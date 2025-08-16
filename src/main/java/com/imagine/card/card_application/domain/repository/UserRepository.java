package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhone(String phone);
    boolean existsByCi(String ci);
    Optional<User> findByPhone(String phone);
    Optional<User> findByCi(String ci);
}

