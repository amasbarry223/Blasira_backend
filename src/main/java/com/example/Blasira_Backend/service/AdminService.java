package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.AdminDashboardStatsDto;
import com.example.Blasira_Backend.dto.admin.DriverApplicationDto;
import com.example.Blasira_Backend.dto.admin.UserDto;
import com.example.Blasira_Backend.exception.UserProfileNotFoundException;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.dto.admin.CreatePromoCodeDto;
import com.example.Blasira_Backend.dto.admin.IncidentStatusUpdateDto;
import com.example.Blasira_Backend.dto.admin.PromoCodeDto;
import com.example.Blasira_Backend.model.IncidentReport;
import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.model.enums.DiscountType;
import com.example.Blasira_Backend.repository.IncidentReportRepository;
import com.example.Blasira_Backend.repository.PromoCodeRepository;
import com.example.Blasira_Backend.dto.admin.UpdateVehicleStatusDto;
import com.example.Blasira_Backend.model.Vehicle;
import com.example.Blasira_Backend.repository.VehicleRepository;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.dto.admin.FinancialReportDto;
import java.time.LocalDate;
import java.util.Map;
import com.example.Blasira_Backend.dto.admin.AppConfigDto;
import com.example.Blasira_Backend.dto.admin.UpdateAppConfigRequest;
import com.example.Blasira_Backend.model.AppConfig;
import com.example.Blasira_Backend.repository.AppConfigRepository;
import com.example.Blasira_Backend.service.IncidentReportService; // Inject IncidentReportService
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final UserProfileRepository userProfileRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final VehicleService vehicleService; // Inject VehicleService
    private final IncidentReportService incidentReportService; // Inject IncidentReportService
    private final PromoCodeRepository promoCodeRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final VehicleRepository vehicleRepository;
    private final AppConfigRepository appConfigRepository;

    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userAccountRepository.count();
        long totalTrips = tripRepository.count();
        long totalBookings = bookingRepository.count();

        return AdminDashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalTrips(totalTrips)
                .totalBookings(totalBookings)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DriverApplicationDto> getPendingDriverApplications() {
        return driverProfileRepository.findByStatus(DriverProfileStatus.PENDING_REVIEW)
                .stream()
                .map(this::mapToDriverApplicationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDriverStatus(Long driverId, DriverProfileStatus newStatus) {
        DriverProfile driverProfile = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new UserProfileNotFoundException("Driver profile not found with id: " + driverId));
        driverProfile.setStatus(newStatus);
        driverProfileRepository.save(driverProfile);
    }

    @Transactional
    public void updateStudentVerifiedStatus(Long userId, boolean isVerified) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found with id: " + userId));

        userProfile.setStudentVerified(isVerified);
        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void updateUserRoles(Long userId, List<com.example.Blasira_Backend.model.enums.Role> newRoles) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User account not found with id: " + userId));
        userAccount.setRoles(new java.util.HashSet<>(newRoles));
        userAccountRepository.save(userAccount);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(userAccount -> UserDto.builder()
                        .id(userAccount.getId())
                        .email(userAccount.getEmail())
                        .firstName(userAccount.getUserProfile() != null ? userAccount.getUserProfile().getFirstName() : null)
                        .lastName(userAccount.getUserProfile() != null ? userAccount.getUserProfile().getLastName() : null)
                        .roles(userAccount.getRoles())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.example.Blasira_Backend.dto.vehicle.VehicleDto> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Transactional(readOnly = true)
    public List<com.example.Blasira_Backend.dto.incident.IncidentReportDto> getAllIncidentReports() {
        return incidentReportService.getAllIncidentReports();
    }

    @Transactional
    public com.example.Blasira_Backend.dto.incident.IncidentReportDto updateIncidentReportStatus(Long reportId, IncidentStatusUpdateDto updateDto) {
        // Appelle la méthode de IncidentReportService qui gère déjà la logique et le mapping
        return incidentReportService.updateIncidentReportStatus(reportId, updateDto.getStatus());
    }

    @Transactional
    public PromoCodeDto createPromoCode(CreatePromoCodeDto promoCodeDto) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(promoCodeDto.getCode());
        promoCode.setDiscountType(DiscountType.valueOf(promoCodeDto.getDiscountType()));
        promoCode.setDiscountValue(BigDecimal.valueOf(promoCodeDto.getDiscountValue()));
        promoCode.setExpiresAt(promoCodeDto.getExpirationDate().toLocalDateTime());
        promoCode.setMaxUses(promoCodeDto.getMaxUses());
        promoCode.setUseCount(0); // Nouveau code promo, utilisé 0 fois
        promoCode.setActive(promoCodeDto.isActive());
        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
        return mapToPromoCodeDto(savedPromoCode);
    }

    @Transactional(readOnly = true)
    public List<PromoCodeDto> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(this::mapToPromoCodeDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateVehicleStatus(Long vehicleId, UpdateVehicleStatusDto updateDto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        vehicle.setVerificationStatus(updateDto.getStatus());
        // Vous pourriez ajouter une logique pour stocker la raison si nécessaire
        vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public List<TripDto> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToTripDto) // Supposons qu'une méthode de mapping existe ou sera créée
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripDto getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));
        return mapToTripDto(trip);
    }

    @Transactional(readOnly = true)
    public FinancialReportDto getFinancialReport() {
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenueConfirmedBookings();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Pour l'exemple, les dépenses sont une estimation simplifiée (ex: 30% du revenu)
        BigDecimal totalExpenses = totalRevenue.multiply(BigDecimal.valueOf(0.30));
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        return FinancialReportDto.builder()
                .reportDate(LocalDate.now())
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .revenueByCategory(Map.of("Trips", totalRevenue)) // Simplifié
                .expensesByCategory(Map.of("Operational", totalExpenses)) // Simplifié
                .build();
    }

    @Transactional(readOnly = true)
    public AppConfigDto getAppConfig() {
        AppConfig config = appConfigRepository.findTopByOrderByIdAsc();
        if (config == null) {
            // Si aucune configuration n'existe, retourner une configuration par défaut ou lancer une exception
            return AppConfigDto.builder()
                    .baseFare(BigDecimal.valueOf(0.0))
                    .pricePerKm(BigDecimal.valueOf(0.0))
                    .defaultCurrency("EUR")
                    .driverValidationRequired(true)
                    .build();
        }
        return mapToAppConfigDto(config);
    }

    @Transactional
    public AppConfigDto updateAppConfig(UpdateAppConfigRequest request) {
        AppConfig config = appConfigRepository.findTopByOrderByIdAsc();
        if (config == null) {
            config = new AppConfig(); // Créer une nouvelle config si elle n'existe pas
        }
        config.setBaseFare(request.getBaseFare());
        config.setPricePerKm(request.getPricePerKm());
        config.setDefaultCurrency(request.getDefaultCurrency());
        config.setDriverValidationRequired(request.isDriverValidationRequired());
        AppConfig updatedConfig = appConfigRepository.save(config);
        return mapToAppConfigDto(updatedConfig);
    }

    private AppConfigDto mapToAppConfigDto(AppConfig config) {
        return AppConfigDto.builder()
                .baseFare(config.getBaseFare())
                .pricePerKm(config.getPricePerKm())
                .defaultCurrency(config.getDefaultCurrency())
                .driverValidationRequired(config.isDriverValidationRequired())
                .build();
    }

    private PromoCodeDto mapToPromoCodeDto(PromoCode promoCode) {
        PromoCodeDto dto = new PromoCodeDto();
        dto.setId(promoCode.getId());
        dto.setCode(promoCode.getCode());
        dto.setDiscountType(promoCode.getDiscountType());
        dto.setDiscountValue(promoCode.getDiscountValue().doubleValue());
        dto.setExpiresAt(promoCode.getExpiresAt().atZone(ZoneOffset.UTC));
        dto.setMaxUses(promoCode.getMaxUses());
        dto.setUseCount(promoCode.getUseCount());
        dto.setActive(promoCode.isActive());
        return dto;
    }

    private TripDto mapToTripDto(Trip trip) {
        TripDto dto = new TripDto();
        dto.setId(trip.getId());
        dto.setDepartureAddress(trip.getDepartureAddress());
        dto.setDestinationAddress(trip.getDestinationAddress());
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setPricePerSeat(trip.getPricePerSeat());
        dto.setAvailableSeats(trip.getAvailableSeats());
        // Mappage des champs spécifiques au TripDto existant
        String driverName = null;
        if (trip.getDriver() != null && trip.getDriver().getUserAccount() != null && trip.getDriver().getUserAccount().getUserProfile() != null) {
            driverName = trip.getDriver().getUserAccount().getUserProfile().getFirstName() + " " + trip.getDriver().getUserAccount().getUserProfile().getLastName();
        }
        dto.setDriverName(driverName);
        dto.setVehicleModel(trip.getVehicle() != null ? trip.getVehicle().getModel() : null);
        return dto;
    }

    private DriverApplicationDto mapToDriverApplicationDto(DriverProfile driverProfile) {
        // Cette méthode de mapping est une simplification. Il faudrait probablement enrichir ce DTO
        // avec plus d'informations venant de UserProfile ou UserAccount si nécessaire.
        return DriverApplicationDto.builder()
                .driverProfileId(driverProfile.getId())
                .userFirstName(driverProfile.getUserAccount().getUserProfile().getFirstName())
                .userLastName(driverProfile.getUserAccount().getUserProfile().getLastName())
                .userEmail(driverProfile.getUserAccount().getEmail())
                .status(driverProfile.getStatus())
                .build();
    }
}
