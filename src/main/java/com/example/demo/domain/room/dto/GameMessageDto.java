package com.example.demo.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMessageDto {

    public enum MessageType{
        ENTER, TALK, TURN ,
        CREATE, JOIN, LEAVE, MATCHING,EXIT,       //talk = 음성
        READY, START, ENDGAME
    }

    private String roomId;
    private String sender;
    private String content;
    private MessageType type;

    public void setMessage(String sender) {
        this.content = sender + "님이 입장했습니다";
    }

}
