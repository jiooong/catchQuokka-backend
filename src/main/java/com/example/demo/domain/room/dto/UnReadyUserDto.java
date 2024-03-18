package com.example.demo.domain.room.dto;

import com.example.demo.domain.user.dto.UserSimpleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnReadyUserDto {
    private String roomId;
    private String userId;
    private List<UserSimpleDto> userSimpleDtos;
    private GameMessageDto.MessageType type;
}
