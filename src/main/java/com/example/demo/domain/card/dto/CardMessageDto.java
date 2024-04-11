package com.example.demo.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardMessageDto {

    public enum MessageType {
        CHECK, DISTINCT, PICK, SHUFFLE, ENDTURN, DEAL
    }

    private String roomId;
    private String sender;
    private String content;
    private MessageType type;

    //다음 사람 정보
    private String NextUser;
    private Integer nextCardNum;
}
