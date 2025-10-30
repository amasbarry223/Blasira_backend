package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.admin.*;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.dto.incident.IncidentReportDto;
import com.example.Blasira_Backend.model.enums.IncidentReportStatus;
import com.example.Blasira_Backend.model.enums.Role;
import com.example.Blasira_Backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour les fonctionnalités d'administration.
 * L'accès à tous les endpoints de ce contrôleur est restreint au rôle "ADMIN"
 * comme défini dans la classe SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Récupère les statistiques pour le tableau de bord de l'administrateur.
     * @return Un DTO contenant diverses statistiques (nombre d'utilisateurs, de trajets, etc.).
     */
    @GetMapping("/dashboard-stats")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    /**
     * Récupère toutes les demandes de validation de profil conducteur qui sont en attente.
     * @return Une liste de demandes de conducteurs.
     */
    @GetMapping("/driver-applications")
    public ResponseEntity<List<DriverApplicationDto>> getPendingDriverApplications() {
        return ResponseEntity.ok(adminService.getPendingDriverApplications());
    }

    /**
     * Met à jour le statut d'un profil de conducteur (ex: Approuver ou Rejeter une demande).
     * @param driverId L'ID du profil conducteur à mettre à jour. L'ID du profil conducteur à mettre à jour.
     * @param request Le nouveau statut à appliquer.
     * @return Une réponse vide si l'opération réussit.
     */
    @PostMapping("/drivers/{driverId}/status")
    public ResponseEntity<Void> updateDriverStatus(@PathVariable Long driverId, @RequestBody UpdateDriverStatusRequest request) {
        adminService.updateDriverStatus(driverId, request.getStatus());
        return ResponseEntity.ok().build();
    }

    /**
     * Met à jour le statut de vérification "étudiant" d'un utilisateur.
     * @param userId L'ID de l'utilisateur à mettre à jour.
     * @param request Le nouveau statut de vérification.
     * @return Une réponse vide si l'opération réussit.
     */
    @PostMapping("/users/{userId}/student-verification")
    public ResponseEntity<Void> updateUserStudentVerification(@PathVariable Long userId, @RequestBody UpdateStudentVerifiedRequest request) {
        adminService.updateStudentVerifiedStatus(userId, request.isStudentVerified());
        return ResponseEntity.ok().build();
    }

    /**
     * Met à jour les rôles d'un utilisateur.
     * @param userId L'ID de l'utilisateur à mettre à jour.
     * @param request La liste des nouveaux rôles à attribuer à l'utilisateur.
     * @return Une réponse vide si l'opération réussit.
     */
    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<Void> updateUserRoles(@PathVariable Long userId, @RequestBody UpdateUserRolesRequest request) {
        adminService.updateUserRoles(userId, request.getRoles());
        return ResponseEntity.ok().build();
    }

    /**
     * Récupère la liste de tous les utilisateurs.
     * @return Une liste de DTOs d'utilisateurs.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<com.example.Blasira_Backend.dto.vehicle.VehicleDto>> getAllVehicles() {
        return ResponseEntity.ok(adminService.getAllVehicles());
    }

    @PutMapping("/vehicles/{vehicleId}/status")
    public ResponseEntity<Void> updateVehicleStatus(@PathVariable Long vehicleId, @RequestBody UpdateVehicleStatusDto updateDto) {
        adminService.updateVehicleStatus(vehicleId, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupère la liste de tous les trajets.
     * @return Une liste de DTOs de trajets.
     */
    @GetMapping("/trips")
    public ResponseEntity<List<TripDto>> getAllTrips() {
        return ResponseEntity.ok(adminService.getAllTrips());
    }

    /**
     * Récupère les détails d'un trajet spécifique par son ID.
     * @param tripId L'ID du trajet.
     * @return Le DTO du trajet.
     */
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDto> getTripById(@PathVariable Long tripId) {
        return ResponseEntity.ok(adminService.getTripById(tripId));
    }

    /**
     * Récupère un rapport financier.
     * @return Un DTO contenant les données du rapport financier.
     */
    @GetMapping("/reports/financial")
    public ResponseEntity<FinancialReportDto> getFinancialReport() {
        return ResponseEntity.ok(adminService.getFinancialReport());
    }

    /**
     * Récupère la configuration actuelle de l'application.
     * @return Le DTO de la configuration de l'application.
     */
    @GetMapping("/config")
    public ResponseEntity<AppConfigDto> getAppConfig() {
        return ResponseEntity.ok(adminService.getAppConfig());
    }

    /**
     * Met à jour la configuration de l'application.
     * @param request Le DTO contenant les nouvelles valeurs de configuration.
     * @return Le DTO de la configuration de l'application mise à jour.
     */
    @PutMapping("/config")
    public ResponseEntity<AppConfigDto> updateAppConfig(@RequestBody UpdateAppConfigRequest request) {
        return ResponseEntity.ok(adminService.updateAppConfig(request));
    }

    /**
     * Récupère tous les rapports d'incidents.
     * @return Une liste de DTOs de rapports d'incidents.
     */
    @GetMapping("/incident-reports")
    public ResponseEntity<List<IncidentReportDto>> getAllIncidentReports() {
        return ResponseEntity.ok(adminService.getAllIncidentReports());
    }

    /**
     * Met à jour le statut d'un rapport d'incident.
     * @param reportId L'ID du rapport d'incident à mettre à jour.
     * @param newStatus Le nouveau statut à appliquer.
     * @return Le DTO du rapport d'incident mis à jour.
     */
    @PutMapping("/incident-reports/{reportId}/status")
    public ResponseEntity<IncidentReportDto> updateIncidentReportStatus(
            @PathVariable Long reportId,
            @RequestBody IncidentStatusUpdateDto updateDto) {
        IncidentReportDto updatedReport = adminService.updateIncidentReportStatus(reportId, updateDto);
        return ResponseEntity.ok(updatedReport);
    }

    /**
     * Crée un nouveau code promo.
     * @param createPromoCodeDto Le DTO contenant les informations du code promo à créer.
     * @return Le DTO du code promo créé.
     */
    @PostMapping("/promo-codes")
    public ResponseEntity<PromoCodeDto> createPromoCode(@RequestBody CreatePromoCodeDto createPromoCodeDto) {
        PromoCodeDto newPromoCode = adminService.createPromoCode(createPromoCodeDto);
        return new ResponseEntity<>(newPromoCode, HttpStatus.CREATED);
    }

    /**
     * Récupère la liste de tous les codes promo.
     * @return Une liste de DTOs de codes promo.
     */
    @GetMapping("/promo-codes")
    public ResponseEntity<List<PromoCodeDto>> getAllPromoCodes() {
        return ResponseEntity.ok(adminService.getAllPromoCodes());
    }
}
