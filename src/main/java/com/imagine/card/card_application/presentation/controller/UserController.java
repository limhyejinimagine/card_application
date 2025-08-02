package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.application.service.UserService;
import com.imagine.card.card_application.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록 API
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // 전화번호로 사용자 조회 API
    @PostMapping("/getUserPhone")
    public ResponseEntity<User> getUserPhone(@RequestBody Map<String,String> request) {

        System.out.println(" getUserPhone 호출: " + request);


        String phone = request.get("phone");

         return userService.findByPhone(phone)
                .map(ResponseEntity::ok)                     // 유저있으면 200 ok + User 객체 반환
                .orElse(ResponseEntity.notFound().build());  // 없으면 404 Not Found
    }
}
