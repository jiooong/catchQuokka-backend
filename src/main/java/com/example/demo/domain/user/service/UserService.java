package com.example.demo.domain.user.service;

import com.example.demo.domain.room.dto.RoomDto;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.user.dto.LoginRequestDto;
import com.example.demo.domain.user.dto.ProfileResponseDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.entity.UserRoleEnum;
import com.example.demo.domain.user.entity.UserSocialEnum;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.jwt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ProfileResponseDto profile(User user) {
        return ProfileResponseDto.builder()
                .name(user.getName())
                .exp(user.getExp())
                .level(user.getLevel()).build();
    }

    //테스트용이라 나중에 삭제
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) {
        try {
            String uuid = UUID.randomUUID().toString();
            User user = new User(uuid, loginRequestDto.getEmail(), loginRequestDto.getPassword(), UserSocialEnum.NAVER, UserRoleEnum.USER);

            String accessToken = jwtUtil.createAccessToken(loginRequestDto.getPassword(), UserRoleEnum.USER);
            userRepository.save(user);

            return ResponseEntity.ok(accessToken);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //로그인시 로그인된 정보를 db에 저장해야만 닉네임 변경이 가능

    //테스트용 푸쉬전 삭제
    public ResponseEntity<?> test(RoomDto roomDto) {
        List<String> userIds = userRepository.findUserIdsByRoomRoomId(roomDto.getRoomId());

//        List<String> userIds = new ArrayList<>();
        userIds.add("1");
        userIds.add("2");
        userIds.add("3");
        userIds.add("4");
        Collections.shuffle(userIds); // 플레이어들의 순서를 무작위로 섞습니다.
        Queue<String> queue = new ArrayDeque<>(userIds); // 플레이어들을 큐에 넣습니다.
        List<String> playerOrder = new ArrayList<>(); // 플레이어 순서를 저장할 리스트를 초기화합니다.

        // 큐가 빌 때까지 반복하여 플레이어 순서를 결정합니다.
        while (!queue.isEmpty()) {
            String nextPlayer = queue.poll(); // 큐의 맨 앞 플레이어를 꺼냅니다.
            playerOrder.add(nextPlayer); // 플레이어 순서 리스트에 추가합니다.
        }

        return ResponseEntity.ok(playerOrder);
    }
}
