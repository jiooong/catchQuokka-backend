package com.example.demo.domain.user.entity;

import com.example.demo.domain.room.entity.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @Column(name = "user_id")
    private String userId = String.valueOf(UUID.randomUUID());//소셜로그인 연동 후 삭제 필요

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserSocialEnum social;

    @Column(nullable = false)
    private Long level;

    @Column(nullable = false)
    private Long exp;

    @Column(nullable = false)
    private boolean ready;

    @Column(nullable = false)
    private boolean done;

    //게임중인지도 판별해서 넣어줄건가요?
    //준비중인거까진 필요한거 같은데 게임이 끝났을때 done값도 필요한가???

//    @OneToMany
//    @JoinColumn(name = "card_id")
//    private List<Card> cards;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public User(String userId, String name, String email, UserSocialEnum userSocialEnum, UserRoleEnum roleEnum) {
        userId = userId;        //임시로 바꿨음 원복해둘것
        this.name = name;
        this.email = email;
        this.level = 0L;
        this.exp = 0L;
        this.social = userSocialEnum;
        this.role = roleEnum;
    }

    public void joinUser(Room room) {
        this.room = room;
    }

    public void setReady() {
        this.ready = !(this.ready);
    }

    public void setDone() {
        this.done = true;
    }

    public void leave() {
        this.room = null;
        this.ready = false;
        this.done = false;
    }
}
