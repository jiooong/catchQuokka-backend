package com.example.demo.domain.room.controller;

import com.example.demo.domain.room.service.RoomService;
import com.example.demo.global.jwt.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "예제 API", description = "Swagger 테스트용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
  
    //사설 방 생성
    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.create(userDetails.getUser());
    }

//    //방 입장 필요한가?
//    @GetMapping("/")

    //자동 매칭 시스템
    @PostMapping("/match")
    public ResponseEntity<?> match(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.match(userDetails.getUser());
    }

    //방 코드를 사용한 입장
    @GetMapping("/join/{roomId}")
    public ResponseEntity<?> join(@PathVariable String roomId,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.join(roomId, userDetails.getUser());
    }

    //게임 준비 버튼(방장 제외)
    @PostMapping("/ready")
    public ResponseEntity<?> ready(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.ready(userDetails.getUser());
    }

    //게임 시작 & 53장 카드 배분 & 시작 플레이어 정하기
    @PostMapping("/start/{roomId}")
    public ResponseEntity<?> start(@PathVariable String roomId) {
        return roomService.start(roomId);
    }

    //방 나가기(사람이 없을시 방 폭파)(방장 넘기기 랜덤)
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<?> leave(@PathVariable String roomId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.leave(roomId, userDetails.getUser());
    }


}
