package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
}
