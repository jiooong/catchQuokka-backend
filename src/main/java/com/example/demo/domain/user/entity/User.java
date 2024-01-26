package com.example.demo.domain.user.entity;

import com.example.demo.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int exp;

    @Builder
    public User(String nickname, int level, int exp){
        this.nickname = nickname;
        this.level = level;
        this.exp = exp;
    }

}
