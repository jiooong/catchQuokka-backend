package com.example.demo.domain.card.dto;

import com.example.demo.domain.card.entity.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PickedRequestDto {
    private Integer cardNum;
}
