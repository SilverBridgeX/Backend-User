package com.example.silverbridgeX_user.user.domain;

import com.example.silverbridgeX_user.global.entity.BaseEntity;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "protector_id")
    private User protector;

    @OneToMany(mappedBy = "protector", cascade = CascadeType.ALL)
    private List<User> olders = new ArrayList<>();

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

    @Column(name = "activity_embedding_avg", columnDefinition = "vector(384)", insertable = false, updatable = false)
    private String activityEmbeddingAvg;

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

    public void updateAddress(String address) {
        this.streetAddress = address;
    }

    public void updatePreferredKeywords(List<String> keywords) {
        this.preferredKeywords = keywords;
    }

    public void updateCoordinate(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void updateProtector(User protector) {
        this.protector = protector; // null 가능
    }
}


