package com.example.demo.domain.card.repository;

import com.example.demo.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    //해당 유저가 가지고 있는 card들을 전부 조회해야됨(
    //arraylist난 linked중에 머 쓸지 고민해봐야될듯?
    Card findCardByUserUserId(String userId);
    List<Integer> findCardNumByUserUserId(String userId);
}
