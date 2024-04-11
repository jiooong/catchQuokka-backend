package com.example.demo.domain.user.controller;

import com.example.demo.domain.room.dto.RoomDto;
import com.example.demo.domain.user.dto.LoginRequestDto;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.jwt.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "예제 API", description = "Swagger 테스트용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return null;
    }

    //내 프로필
    @GetMapping("/profile")
    public ResponseEntity<?> profile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.profile(userDetails.getUser()));
    }

    //내 프로필 닉네임만 변경 가능
    @PatchMapping("/modify/nickname")
    public ResponseEntity<?> modifyNickname() {
        return null;
    }

    //테스트용 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(userService.login(loginRequestDto));
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(userService.test(roomDto));
    }
}
