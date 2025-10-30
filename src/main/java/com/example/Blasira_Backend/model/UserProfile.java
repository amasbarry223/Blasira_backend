package com.example.Blasira_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String profilePictureUrl;

    @Column(columnDefinition = "longtext")
    private String bio;

    private LocalDateTime memberSince;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude // Exclure pour éviter StackOverflowError
    @EqualsAndHashCode.Exclude // Exclure pour éviter StackOverflowError
    private UserAccount userAccount;

    @Column(nullable = false)
    private boolean studentVerified = false;
}
