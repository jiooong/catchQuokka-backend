package com.example.demo.domain.room.service;

import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<?> create(User user) {
        try {
            String uuid = UUID.randomUUID().toString();
            Room room = new Room(uuid, user.getUserId(), false);
            roomRepository.save(room);

            user.joinUser(room);
            userRepository.save(user);

            return ResponseEntity.ok(room.getRoomId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> match(User user) {     //방없으면 자동 방 생성도 넣어야됨
        try {
            String roomId = roomRepository.findRoomIdByCreatedAsc();
            if (roomId == null) {
                String uuid = UUID.randomUUID().toString();
                Room room = new Room(uuid, user.getUserId(), true);
                roomRepository.save(room);

                user.joinUser(room);
                userRepository.save(user);

                return ResponseEntity.ok(room.getRoomId());
            }

            return ResponseEntity.ok(roomId);     //수정해야겠지? 배포 주소로 바꿔두고
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> join(String roomId, User user) {
        try {
            Room room = roomRepository.findByRoomId(roomId);
            if (room.getRoomId().isEmpty()) {
                return new ResponseEntity<>("해당 방은 존재하지 않습니다.", HttpStatusCode.valueOf(400));  //결과값에 에러코드 넣을까?
            }
            if (room.getHeadCount() == 6L) {
                return new ResponseEntity<>("방에 빈 자리가 없습니다.", HttpStatusCode.valueOf(400));     //if 문이 많나?
            }
            //roomid 입장한 user들 보여줘야됨
            user.joinUser(room);
            room.joinRoom();

            userRepository.save(user);
            roomRepository.save(room);

            List<User> users = userRepository.findUsersByRoomRoomId(roomId);

            return new ResponseEntity(HttpStatusCode.valueOf(200)).ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }   //나중에 리턴값 유저의 특정 정보만 돌려주기(이름, 프로필?)

    public ResponseEntity<?> ready(User user) {
        try {
            user.setReady();
            userRepository.save(user);

            return new ResponseEntity(HttpStatusCode.valueOf(200)).ok("준비");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }       //준비와 시작은 웹소켓 사용    //준비 취소도 해야되나 확인해보자

    public ResponseEntity<?> start(String roomId) {
        try {
            Room room = roomRepository.findByRoomId(roomId);
            if (3 > room.getHeadCount() || 6 < room.getHeadCount()) {
                return new ResponseEntity(HttpStatusCode.valueOf(400)).badRequest().body("게임을 시작하기 위한 인원은 3~6명입니다.");
            }
            //시작시 카드 배분으로 넘겨야함
            //순서는 프론트에서 저장하고 있도록
            // 준비 다되면 방장이 시작할 수 있도록
            int random= (int) (Math.random() * room.getHeadCount());
            List<User> users = userRepository.findUsersByRoomRoomId(roomId);

            String firstStart = users.get(random).getUserId();

            return new ResponseEntity(HttpStatusCode.valueOf(200)).ok(firstStart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> leave(String roomId, User user) { //떠나면 방인수 줄이고 해당인원 없애는거 생각해야되는듯 나중에 체크
        try {
            Room room = roomRepository.findByRoomId(roomId);
            if (room.getHeadCount() == 1) {
                roomRepository.deleteRoomByRoomId(roomId);
                return null;
            }
            user.leave();
            int random= (int) (Math.random() * room.getHeadCount());
            List<User> users = userRepository.findUsersByRoomRoomId(roomId);
            String newManager = users.get(random).getUserId();
            room.changeManager(newManager);
            roomRepository.save(room);
            userRepository.save(user);

            return null;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }       //새로고침이나 강제종료후 처음부터 접속하면 초기화를 어떻게 시켜야되지? (로그인시 해당 유저 정보가 저장된 방이 있는지 체크해보면 될듯?)

    public ResponseEntity<?> createRoom() {
        String randomId = UUID.randomUUID().toString();
        Room gameRoom = new Room(randomId);
        roomRepository.save(gameRoom);
        return ResponseEntity.ok(gameRoom);
    }

    public Room findRoomById(String roomId) {
        return roomRepository.findByRoomId(roomId);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
