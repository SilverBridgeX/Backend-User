package com.example.silverbridgeX_user.user.domain;

import com.example.silverbridgeX_user.global.entity.BaseEntity;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.MatchResult;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    private String sex;

    private String birth;

    private String streetAddress;

    private String latitude;

    private String longitude;

    private String profileImage;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MatchRequest matchRequest;

    public User(String username, String nickname, String email) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}


