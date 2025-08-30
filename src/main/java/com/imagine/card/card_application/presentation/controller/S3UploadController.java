package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.application.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3UploadController {

    private final S3UploadService s3UploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file)  throws IOException {

        System.out.println("📥 S3UploadController 진입 성공");
        System.out.println("📄 파일 이름: " + file.getOriginalFilename());
        System.out.println("📂 Content-Type: " + file.getContentType());
        System.out.println("📦 크기: " + file.getSize());

        String url = s3UploadService.upload(file);
        System.out.println("✅ 업로드 완료: URL = " + url);
        return ResponseEntity.ok(url);
    }
}
