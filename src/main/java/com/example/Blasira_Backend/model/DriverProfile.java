package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "driver_profiles")
public class DriverProfile {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverProfileStatus status = DriverProfileStatus.NOT_SUBMITTED;

    private int totalTripsDriven = 0;

    private Double averageRating = 0.0;

    private long ratingSum = 0;

    private int reviewCount = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UserAccount userAccount;

    @OneToMany(mappedBy = "driverProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();
}
