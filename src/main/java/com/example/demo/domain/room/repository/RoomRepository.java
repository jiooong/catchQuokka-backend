package com.example.demo.domain.room.repository;

import com.example.demo.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomId(String roomId);


    //서브 쿼리를 이용해서 좀더 효율적으로 할 수 없을까?     //남는 방이 없으면 자동 생성
    @Query(value = "SELECT r.roomId FROM Room r WHERE r.headCount < 6 AND r.matching = true ORDER BY r.created asc LIMIT 1")
    String findRoomIdByCreatedAsc();

    @Query(value = "DELETE FROM Room r WHERE r.roomId = :roomId")
    Room deleteRoomByRoomId(String roomId);
}
