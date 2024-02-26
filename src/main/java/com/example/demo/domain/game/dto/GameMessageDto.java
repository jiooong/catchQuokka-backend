package com.example.demo.domain.game.dto;

import com.example.demo.domain.card.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMessageDto {

    private String roomId;
    private String sender;
    private String content;
    private MessageType type;
//    private List<Card> cards;

    public enum MessageType {
        CREATE, JOIN, LEAVE, MATCHING, TALK,        //talk = 음성
        READY, START, ENDTURN, ENDGAME,
        CHECK, DISTINCT, PICK, SHUFFLE,
        PRECHECK, DRAW, ENDDRAW, SELECT, TURNCHECK, USEFAIL, USECARD, USESPECIAL, DISCARD,
        UPDATE, SWITCHING
    }
}
