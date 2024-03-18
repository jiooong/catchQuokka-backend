package com.example.demo.domain.user.repository;

import com.example.demo.domain.card.entity.Card;
import com.example.demo.domain.room.entity.Room;
import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);   //username이랑 섞인거 아니겠지?
    Room findRoomByUserId(String userId);
    User findByUserId(String userId); //이메일로 찾아야될듯?
//    ArrayList<Card> findCardsByUserId(String userId);

    List<User> findUsersByRoomRoomId(String roomId);

    List<User> findUsersByRoomRoomIdAndReadyFalse(String roomId);
}
