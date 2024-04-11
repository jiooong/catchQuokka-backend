package com.example.demo.domain.card.dto;

import com.example.demo.domain.card.entity.Card;
import com.example.demo.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardUserResponseDto {
    List<User> users;
    List<Card> cards;
}
