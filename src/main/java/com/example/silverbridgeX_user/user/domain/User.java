package com.example.silverbridgeX_user.user.domain;

import com.example.silverbridgeX_user.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> preferredKeywords;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> dislikeKeywords;

    @Column(columnDefinition = "vector(384)")
    @Transient
    private String preferredEmbedding;

    @Column(columnDefinition = "vector(384)")
    @Transient
    private String dislikeEmbedding;

    public User(String username, String nickname, String email) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}


