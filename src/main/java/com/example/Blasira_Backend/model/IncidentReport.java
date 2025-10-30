package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.IncidentReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "incident_reports")
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserProfile reporter;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "longtext")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentReportStatus status = IncidentReportStatus.OPEN;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
