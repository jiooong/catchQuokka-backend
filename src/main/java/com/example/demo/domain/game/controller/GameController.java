package com.example.demo.domain.game.controller;

import com.example.demo.domain.card.dto.CardMessageDto;
import com.example.demo.domain.card.entity.Card;
import com.example.demo.domain.card.repository.CardRepository;
import com.example.demo.domain.card.service.CardService;
import com.example.demo.domain.room.dto.GameMessageDto;
import com.example.demo.domain.room.dto.UnReadyUserDto;
import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.room.repository.RoomRepository;
import com.example.demo.domain.room.service.RoomService;
import com.example.demo.domain.user.dto.UserSimpleDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class GameController {
    private final UserRepository userRepository;
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final RabbitTemplate rabbitTemplate;

    //        private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("game.{roomId}")
    public void gameMessageProxy(@DestinationVariable("roomId") String roomId, @Payload GameMessageDto message, java.security.Principal principal) throws JsonProcessingException {
        System.out.println("여기에 들어오나 메시지매핑 메서드");

        //방장만 시작가능하면 방장 제외모두 ready 상태
        if (GameMessageDto.MessageType.START.equals(message.getType())) {
            System.out.println("게임 시작" + message.getType());
            gameStarter(roomId, message);
        }
        if (GameMessageDto.MessageType.READY.equals(message.getType())) {
            System.out.println("게임 대기" + message.getType());
            gameReady(roomId, message);
        }
        if (GameMessageDto.MessageType.ENDGAME.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType()); // 게임 종료 후 남은 사람 1명 반환해줘야 하는건지
            gameEnd(roomId,message);
        } // 방생성은 필요없을거 같아서 지움
        if (GameMessageDto.MessageType.JOIN.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            roomJoin(roomId,message);
        }
        // 게임중간에 탈주 -> 게임 자체가 끝난다
        if (GameMessageDto.MessageType.LEAVE.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            roomLeave(roomId,message);
        }
        if (GameMessageDto.MessageType.MATCHING.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            roomMatching(message);
        }
    }

    @MessageMapping("/card/{roomId}")
    public void cardMessageProxy(@Payload CardMessageDto message) throws JsonProcessingException {
        System.out.println("여기에 들어오나 메시지매핑 메서드");

        if (CardMessageDto.MessageType.ENDTURN.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            turnEnd(message);
        }
        if (CardMessageDto.MessageType.CHECK.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            cardCheck(message);
        }
        if (CardMessageDto.MessageType.DISTINCT.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            cardDistinct(message);
        }
        if (CardMessageDto.MessageType.PICK.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            cardPick(message);
        }
        if (CardMessageDto.MessageType.SHUFFLE.equals(message.getType())) {
            System.out.println("여기에 들어오나" + message.getType());
            cardShuffle(message);
        }
    }

    public Room findRoom(String roomId){
        Room room = roomRepository.findByRoomId(roomId);
        if(!StringUtils.hasText(roomId)){
            throw new NullPointerException("해당하는 방이 존재하지 않습니다.");
        }else{
            return room;
        }
    }
    public void gameStarter(String roomId, GameMessageDto message) throws JsonProcessingException {
        System.out.println("Game Start");
        Room room = findRoom(roomId);
        roomService.start(room.getRoomId());
        GameMessageDto gameMessage = new GameMessageDto();
        gameMessage.setRoomId(message.getRoomId());
        gameMessage.setSender(message.getSender());
        gameMessage.setType(GameMessageDto.MessageType.START);
        gameMessage.setContent("게임을 시작하겠습니다");
        rabbitTemplate.convertAndSend("game.exchange", "start.room." + roomId, gameMessage);
    }

    // ready 안한 유저 이름만 알려주기!!
    public List<User> countPeople(String roomId, Room room){
        List<User> users = userRepository.findUsersByRoomRoomId(roomId);

        Long checkReady = 0L;
        for (User user : users) {
            if (!user.isReady()) {
                checkReady++;
            }
        }
//        if (checkReady == room.getHeadCount()) {
//            return "start";
//        }
        List<User> notReadyUsers = userRepository.findUsersByRoomRoomIdAndReadyFalse(roomId);
        return notReadyUsers;
    }
    // 준비단계
    private void gameReady(String roomId, GameMessageDto message) {
        System.out.println("Game Ready");
        message.getSender();
        Room room = findRoom(roomId);
        List<User> notReadyUsers = countPeople(room.getRoomId(), room);
        List<UserSimpleDto> userSimpleDtoList = new ArrayList<>();
        for(User i : notReadyUsers){
            UserSimpleDto userSimpleDto = UserSimpleDto.builder()
                    .userId(i.getUserId())
                    .email(i.getEmail())
                    .name(i.getName())
                    .build();
            userSimpleDtoList.add(userSimpleDto);
        }
        UnReadyUserDto unReadyUserDto = UnReadyUserDto.builder()
                .roomId(room.getRoomId())
                .userSimpleDtos(userSimpleDtoList)
                .type(GameMessageDto.MessageType.READY).build();

        rabbitTemplate.convertAndSend("game.exchange", "ready.room." + roomId, unReadyUserDto);
    }

    //필요하나?
    private void turnEnd(CardMessageDto message) {
        System.out.println("여기에 들어오나 플레이어 끝");
        String userId = message.getSender();
        List<Card> cards = cardRepository.findCardsByUserUserId(userId);
        if (cards.isEmpty()) {
            User user = userRepository.findByUserId(userId);
            user.setDone();
        }

//        String messageContent = jsonStringBuilder.gameStarter(game);
        CardMessageDto cardMessage = new CardMessageDto();
        cardMessage.setRoomId(message.getRoomId());
        cardMessage.setSender(message.getSender());
        cardMessage.setType(CardMessageDto.MessageType.ENDTURN);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //경험치 1게임당 이기면 10점 지면 5점씩 증가한다.
    private void gameEnd(String roomId, GameMessageDto message) {
        System.out.println("게임 종료");
        Room room = findRoom(roomId);
        List<User> users = userRepository.findUsersByRoomRoomId(message.getRoomId());
        Long count = 0L;
        for (User u : users) {
            if (!u.isDone()) {
                count++;
            }
        }
        //게임 종료
        if (!(count == (room.getHeadCount() - 1L))) {
            return;    //에러? 아니면 다른 방법?
        }
//        String messageContent = jsonStringBuilder.gameStarter(game);
        GameMessageDto gameMessage = new GameMessageDto();
        gameMessage.setRoomId(message.getRoomId());
        gameMessage.setSender(message.getSender());
        gameMessage.setType(GameMessageDto.MessageType.ENDGAME);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    private void roomJoin(String roomId, GameMessageDto message) {
        System.out.println("여기에 들어오나 방 입장");
        Room room = findRoom(roomId);
        User user = userRepository.findByUserId(message.getSender());

        user.joinUser(room);
        room.joinRoom();

        userRepository.save(user);
        roomRepository.save(room);

//        String messageContent = jsonStringBuilder.gameStarter(game);
        GameMessageDto gameMessage = new GameMessageDto();
        gameMessage.setRoomId(message.getRoomId());
        gameMessage.setSender(message.getSender());
        gameMessage.setType(GameMessageDto.MessageType.JOIN);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    private void roomLeave(String roomId, GameMessageDto message) {
        System.out.println("여기에 들어오나 방 떠나기");
        User user = userRepository.findByUserId(message.getSender());
        Room room = findRoom(roomId);

        room.leaveRoom();
        if (room.getHeadCount() == 0) {
            roomRepository.deleteRoomByRoomId(roomId);
        }
        if (!(room == null)) {
            int randomManager = (int) (Math.random() * room.getHeadCount());
            List<User> users = userRepository.findUsersByRoomRoomId(roomId);
            room.changeManager(users.get(randomManager).getUserId());
        }
        user.leave();

        roomRepository.save(room);
        userRepository.save(user);

//        String messageContent = jsonStringBuilder.gameStarter(game);
        GameMessageDto gameMessage = new GameMessageDto();
        gameMessage.setRoomId(message.getRoomId());
        gameMessage.setSender(message.getSender());
        gameMessage.setType(GameMessageDto.MessageType.LEAVE);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //필요한가?
    private void roomMatching(GameMessageDto message) {
        System.out.println("여기에 들어오나 방 자동 매칭");
        String roomId = roomRepository.findRoomIdByCreatedAsc();
        if (roomId == null) {
            User user = userRepository.findByUserId(message.getSender());
            String uuid = UUID.randomUUID().toString();
            Room room = new Room(uuid, user.getUserId(), true);
            roomRepository.save(room);

            user.joinUser(room);
            userRepository.save(user);
        }

//        String messageContent = jsonStringBuilder.gameStarter(game);
        GameMessageDto gameMessage = new GameMessageDto();
        gameMessage.setRoomId(roomId);
        gameMessage.setSender(message.getSender());
        gameMessage.setType(GameMessageDto.MessageType.MATCHING);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //cards를 보는 용도인데 수정이 필요함
    private void cardCheck(CardMessageDto message) {
        System.out.println("여기에 들어오나 내 카드 조회");
        User user = userRepository.findByUserId(message.getSender());
        String userId = user.getUserId();
        List<Card> cards = cardRepository.findCardsByUserUserId(userId);

//        String messageContent = jsonStringBuilder.gameStarter(game);
        CardMessageDto cardMessage = new CardMessageDto();
        cardMessage.setRoomId(message.getRoomId());
        cardMessage.setSender(message.getSender());
        cardMessage.setType(CardMessageDto.MessageType.CHECK);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //중복 제거 수정 필요
    private void cardDistinct(CardMessageDto message) {
        System.out.println("여기에 들어오나 카드 중복 제거");
        List<Integer> playerHands = cardRepository.findCardNumByUserUserId(message.getSender());
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer hand : playerHands) {
            countMap.put(hand, countMap.getOrDefault(hand, 0) + 1);
        }

        List<Integer> uniqueNumbers = new ArrayList<>();
        for (int number : playerHands) {
            int count = countMap.get(number);
            if (count % 2 == 1 && !uniqueNumbers.contains(number)) {
                uniqueNumbers.add(number);
            }
        }

//        String messageContent = jsonStringBuilder.gameStarter(game);
        CardMessageDto cardMessage = new CardMessageDto();
        cardMessage.setRoomId(message.getRoomId());
        cardMessage.setSender(message.getSender());
        cardMessage.setType(CardMessageDto.MessageType.DISTINCT);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //상대방 카드 뽑기 수정 필요
    private void cardPick(CardMessageDto message) {
        System.out.println("여기에 들어오나 상대 카드 뽑기");
        List<Card> cards = cardRepository.findCardsByUserUserId(message.getSender());
//        cards.add(card);    //카드 어디로 반환해?

//        String messageContent = jsonStringBuilder.gameStarter(game);
        CardMessageDto cardMessage = new CardMessageDto();
        cardMessage.setRoomId(message.getRoomId());
        cardMessage.setSender(message.getSender());
        cardMessage.setType(CardMessageDto.MessageType.PICK);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }

    //수정 필요
    private void cardShuffle(CardMessageDto message) {
        System.out.println("여기에 들어오나 내 카드 섞기");
        List<Card> cards = cardRepository.findCardsByUserUserId(message.getSender());
        Collections.shuffle(cards);

//        String messageContent = jsonStringBuilder.gameStarter(game);
        CardMessageDto cardMessage = new CardMessageDto();
        cardMessage.setRoomId(message.getRoomId());
        cardMessage.setSender(message.getSender());
        cardMessage.setType(CardMessageDto.MessageType.SHUFFLE);
//        gameMessage.setContent(messageContent);
//        messagingTemplate.convertAndSend("/sub/game/" + message.getRoomId(), gameMessage);
    }
}
