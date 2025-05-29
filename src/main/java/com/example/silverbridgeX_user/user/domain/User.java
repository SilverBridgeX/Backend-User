package com.example.silverbridgeX_user.user.domain;

import com.example.silverbridgeX_user.global.entity.BaseEntity;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MatchRequest matchRequest;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> preferredKeywords;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> dislikeKeywords;

    @Column(name = "preferred_embedding", columnDefinition = "vector(384)", insertable = false, updatable = false)
    private String preferredEmbedding;

    @Column(name = "dislike_embedding", columnDefinition = "vector(384)", insertable = false, updatable = false)
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


