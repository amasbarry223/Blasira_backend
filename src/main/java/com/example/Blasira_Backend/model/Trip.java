package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverProfile driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private String departureAddress;

    @Column(nullable = false)
    private String destinationAddress;
    
    // Considérer l'utilisation d'un type Point approprié pour les requêtes GIS si nécessaire
    private String departureCoordinates; 
    private String destinationCoordinates;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private BigDecimal pricePerSeat;

    @Column(nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.PLANNED;
}
