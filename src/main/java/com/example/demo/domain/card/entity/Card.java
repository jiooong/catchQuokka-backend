package com.example.demo.domain.card.entity;

import com.example.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Card {

    @Id
    @Column(name = "card_id")
    private Long cardId;

//    @Column(name = "suit")
//    private String suit;

    @ElementCollection
    @Column(name = "card_num")
    private List<Integer> cardNum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Card(List<Integer> cards, User user) {
        this.cardNum = cards;
        this.user = user;
    }

    public void addCard(List<Integer> cards) {
        this.cardNum = cards;
    }
}
