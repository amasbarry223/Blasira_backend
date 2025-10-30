package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.config.AppConfig;
import com.example.Blasira_Backend.config.DefaultAdminInitializer;
import com.example.Blasira_Backend.controller.BookingController;
import com.example.Blasira_Backend.controller.DriverController;
import com.example.Blasira_Backend.controller.IncidentReportController;
import com.example.Blasira_Backend.dto.admin.CreatePromoCodeDto;
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        AppConfig.class,
        DefaultAdminInitializer.class,
        BookingController.class,
        DriverController.class,
        IncidentReportController.class
    })
)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService; // Mock du service pour isoler le contrôleur

    @MockBean
    private TripRepository tripRepository;
    @MockBean
    private UserAccountRepository userAccountRepository;
    @MockBean
    private VehicleRepository vehicleRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private DriverProfileRepository driverProfileRepository;
    @MockBean
    private UserProfileRepository userProfileRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private PromoCodeRepository promoCodeRepository;
    @MockBean
    private DocumentRepository documentRepository;
    @MockBean
    private IncidentReportRepository incidentReportRepository;

    @Test
    void shouldCreatePromoCodeSuccessfully() throws Exception {
        // 1. Préparation du DTO
        CreatePromoCodeDto promoDto = new CreatePromoCodeDto();
        promoDto.setCode("HIVER2025");
        promoDto.setDiscountType("FIXED_AMOUNT");
        promoDto.setDiscountValue(5.00);
        promoDto.setExpirationDate(ZonedDateTime.parse("2026-03-31T23:59:59Z"));
        promoDto.setMaxUses(1000);
        promoDto.setUsesPerUser(1);
        promoDto.setActive(true);

        // 2. Conversion du DTO en chaîne JSON
        String promoJson = objectMapper.writeValueAsString(promoDto);

        // 3. Simulation de la requête POST
        // Remarque : Ce test suppose que la sécurité est configurée pour les tests.
        // Vous pourriez avoir besoin d'ajouter .with(user("admin").roles("ADMIN")) par exemple.
        mockMvc.perform(post("/api/admin/promo-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(promoJson)
                        .with(csrf())) // Ajout du token CSRF pour la sécurité
                // 4. Vérification du statut de la réponse (201 Created)
                .andExpect(status().isCreated());
    }
}
