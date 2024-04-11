package com.example.demo.domain.room.dto;

import com.example.demo.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private String roomId;
//    private List<User> users;   //이름과 id값만 있어도 될려나?

}
