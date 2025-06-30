package com.example.silverbridgeX_user.user.domain;

import com.example.silverbridgeX_user.global.entity.BaseEntity;
import com.example.silverbridgeX_user.matching.domain.MatchRequest;
import com.example.silverbridgeX_user.payment.domain.Payment;
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
import java.time.LocalDate;
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
    @JoinColumn(name = "guardian_id")
    private User guardian;

    @OneToMany(mappedBy = "guardian", cascade = CascadeType.ALL)
    private List<User> olders = new ArrayList<>();

    private Boolean isSubscribed;

    private String sex;

    private LocalDate birth;

    private String streetAddress;

    private String latitude;

    private String longitude;

    private String profileImage;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MatchRequest matchRequest;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

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

    public void updateGuardian(User guardian) {
        this.guardian = guardian; // null 가능
    }

    public Boolean isSubscribeActive() {
        return isSubscribed;
    }

    public void enableSubscription() {
        this.isSubscribed = true;
    }

    public void disableSubscription() {
        this.isSubscribed = false;
    }
}


