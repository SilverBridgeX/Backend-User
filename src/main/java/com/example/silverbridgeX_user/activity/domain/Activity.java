package com.example.silverbridgeX_user.activity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private ActivityType activityType;

    @Column(length = 1000)
    private String description;

    @Column(columnDefinition = "vector(384)")
    @Transient
    private String descriptionEmbedding;

    private String streetAddress;

    private String lotNumberAddress;

    private String latitude;

    private String longitude;

    private LocalDate startDate;

    private LocalDate endDate;

    private String homepageUrl;

    private String phoneNumber;

    private Long chosen;

    private Long shown;

    private Double CTR;

    public void updateCoordinate(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void updateLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void updateLatitude(String latitude) {
        this.latitude = latitude;
    }

}
