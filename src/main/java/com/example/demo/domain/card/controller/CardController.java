package com.example.demo.domain.card.controller;

import com.example.demo.domain.card.dto.CardsResponseDto;
import com.example.demo.domain.card.dto.CardUserResponseDto;
import com.example.demo.domain.card.dto.PickedRequestDto;
import com.example.demo.domain.card.service.CardService;
import com.example.demo.global.jwt.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "예제 API", description = "Swagger 테스트용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    //카드 조회     //한페이지 내에서 각 유저의 남은 카드 장수를 각각 조회해서 프론트가 확인하는걸까?
    @GetMapping("/check")
    public CardsResponseDto check(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.check(userDetails.getUser().getUserId());
    }

    //중복 제거(시작시)
    @PostMapping("/start/distinct/{roomId}")
    public CardUserResponseDto startDistinct(@PathVariable String roomId) {
        return cardService.startDistinct(roomId);
    }

    //카드 뽑기 & 카드 중복 제거 & 종료된 사람 순서 스킵 & 종료 & 경험치
    @PostMapping("/distinct")
    public ResponseEntity<?> distinct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               PickedRequestDto pickedRequestDto) {
        return cardService.distinct(pickedRequestDto.getCardNum(), userDetails.getUser());
    }

    //카드 뽑을때 섞기
    @PostMapping("/shuffle")
    public ResponseEntity<?> shuffle(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.shuffle(userDetails.getUser().getUserId());
    }

}
