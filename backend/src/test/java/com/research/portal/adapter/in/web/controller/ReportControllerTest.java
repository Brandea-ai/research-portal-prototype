package com.research.portal.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.in.web.dto.CreateReportRequest;
import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.model.*;
import com.research.portal.domain.port.in.GetReportsUseCase;
import com.research.portal.domain.port.in.ManageReportUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests für ReportController.
 * Testet HTTP Endpoints mit MockMvc.
 */
@WebMvcTest(ReportController.class)
@Import({ReportApiMapper.class, GlobalExceptionHandler.class})
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetReportsUseCase getReportsUseCase;

    @MockitoBean
    private ManageReportUseCase manageReportUseCase;

    // Hilfsmethode: Erstellt ein Domain-Objekt
    private ResearchReport createDomainReport(Long id, String title) {
        ResearchReport report = new ResearchReport();
        report.setId(id);
        report.setAnalystId(1L);
        report.setSecurityId(1L);
        report.setPublishedAt(LocalDateTime.of(2026, 2, 22, 10, 0));
        report.setReportType(ReportType.UPDATE);
        report.setTitle(title);
        report.setExecutiveSummary("Zusammenfassung");
        report.setFullText("Volltext");
        report.setRating(Rating.BUY);
        report.setPreviousRating(Rating.HOLD);
        report.setRatingChanged(true);
        report.setTargetPrice(new BigDecimal("120.50"));
        report.setPreviousTarget(new BigDecimal("100.00"));
        report.setCurrentPrice(new BigDecimal("105.00"));
        report.setImpliedUpside(new BigDecimal("14.76"));
        report.setRiskLevel(RiskLevel.MEDIUM);
        report.setInvestmentCatalysts(List.of("Starkes Q4"));
        report.setKeyRisks(List.of("Regulierung"));
        report.setTags(List.of("Healthcare"));
        return report;
    }

    // Hilfsmethode: Erstellt einen gültigen CreateReportRequest
    private CreateReportRequest createValidRequest() {
        CreateReportRequest request = new CreateReportRequest();
        request.setAnalystId(1L);
        request.setSecurityId(1L);
        request.setReportType("UPDATE");
        request.setTitle("Nestlé Q4 Update");
        request.setExecutiveSummary("Starkes Quartal");
        request.setFullText("Detaillierte Analyse...");
        request.setRating("BUY");
        request.setPreviousRating("HOLD");
        request.setTargetPrice(new BigDecimal("120.50"));
        request.setCurrentPrice(new BigDecimal("105.00"));
        request.setRiskLevel("MEDIUM");
        request.setInvestmentCatalysts(List.of("Starkes Q4"));
        request.setKeyRisks(List.of("Regulierung"));
        request.setTags(List.of("Healthcare"));
        return request;
    }

    @Nested
    @DisplayName("GET /api/reports")
    class GetAllReports {

        @Test
        @DisplayName("Gibt 200 und Liste aller Reports zurück")
        void shouldReturnAllReports() throws Exception {
            var reports = List.of(
                    createDomainReport(1L, "Nestlé Analyse"),
                    createDomainReport(2L, "Novartis Update")
            );
            when(getReportsUseCase.getAllReports()).thenReturn(reports);

            mockMvc.perform(get("/api/reports"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Nestlé Analyse"))
                    .andExpect(jsonPath("$[0].rating").value("BUY"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].title").value("Novartis Update"));
        }

        @Test
        @DisplayName("Gibt 200 und leere Liste zurück wenn keine Reports existieren")
        void shouldReturnEmptyList() throws Exception {
            when(getReportsUseCase.getAllReports()).thenReturn(List.of());

            mockMvc.perform(get("/api/reports"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/reports/{id}")
    class GetReportById {

        @Test
        @DisplayName("Gibt 200 und Report zurück wenn ID existiert")
        void shouldReturnReportWhenFound() throws Exception {
            var report = createDomainReport(1L, "Nestlé Analyse");
            when(getReportsUseCase.getReportById(1L)).thenReturn(Optional.of(report));

            mockMvc.perform(get("/api/reports/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Nestlé Analyse"))
                    .andExpect(jsonPath("$.rating").value("BUY"))
                    .andExpect(jsonPath("$.reportType").value("UPDATE"))
                    .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                    .andExpect(jsonPath("$.ratingChanged").value(true))
                    .andExpect(jsonPath("$.targetPrice").value(120.50))
                    .andExpect(jsonPath("$.investmentCatalysts[0]").value("Starkes Q4"));
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn ID nicht existiert")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getReportsUseCase.getReportById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/reports/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Nested
    @DisplayName("GET /api/reports?analystId=")
    class GetReportsByAnalyst {

        @Test
        @DisplayName("Gibt Reports eines bestimmten Analysten zurück")
        void shouldReturnReportsByAnalyst() throws Exception {
            var reports = List.of(createDomainReport(1L, "Analyst Report"));
            when(getReportsUseCase.getReportsByAnalyst(1L)).thenReturn(reports);

            mockMvc.perform(get("/api/reports").param("analystId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Analyst Report"));
        }
    }

    @Nested
    @DisplayName("GET /api/reports?securityId=")
    class GetReportsBySecurity {

        @Test
        @DisplayName("Gibt Reports einer bestimmten Wertschrift zurück")
        void shouldReturnReportsBySecurity() throws Exception {
            var reports = List.of(createDomainReport(1L, "Security Report"));
            when(getReportsUseCase.getReportsBySecurity(1L)).thenReturn(reports);

            mockMvc.perform(get("/api/reports").param("securityId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Security Report"));
        }
    }

    @Nested
    @DisplayName("POST /api/reports")
    class CreateReport {

        @Test
        @DisplayName("Gibt 201 und erstellten Report zurück")
        void shouldCreateReport() throws Exception {
            var savedDomain = createDomainReport(1L, "Nestlé Q4 Update");
            when(manageReportUseCase.createReport(any(ResearchReport.class))).thenReturn(savedDomain);

            var request = createValidRequest();

            mockMvc.perform(post("/api/reports")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Nestlé Q4 Update"))
                    .andExpect(jsonPath("$.rating").value("BUY"));
        }

        @Test
        @DisplayName("Gibt 400 zurück bei fehlendem Titel")
        void shouldReturn400WhenTitleMissing() throws Exception {
            var request = createValidRequest();
            request.setTitle("");  // Titel leer = @NotBlank Verletzung

            mockMvc.perform(post("/api/reports")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Gibt 400 zurück bei fehlendem Rating")
        void shouldReturn400WhenRatingMissing() throws Exception {
            var request = createValidRequest();
            request.setRating("");  // Rating leer

            mockMvc.perform(post("/api/reports")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Gibt 400 zurück bei fehlendem Kursziel")
        void shouldReturn400WhenTargetPriceMissing() throws Exception {
            var request = createValidRequest();
            request.setTargetPrice(null);

            mockMvc.perform(post("/api/reports")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Gibt 400 zurück bei negativem Kursziel")
        void shouldReturn400WhenTargetPriceNegative() throws Exception {
            var request = createValidRequest();
            request.setTargetPrice(new BigDecimal("-10.00"));

            mockMvc.perform(post("/api/reports")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/reports/{id}")
    class UpdateReport {

        @Test
        @DisplayName("Gibt 200 und aktualisierten Report zurück")
        void shouldUpdateReport() throws Exception {
            var updatedDomain = createDomainReport(1L, "Aktualisierter Report");
            when(manageReportUseCase.updateReport(eq(1L), any(ResearchReport.class)))
                    .thenReturn(updatedDomain);

            var request = createValidRequest();
            request.setTitle("Aktualisierter Report");

            mockMvc.perform(put("/api/reports/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Aktualisierter Report"));
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn Report nicht existiert")
        void shouldReturn404WhenNotFound() throws Exception {
            when(manageReportUseCase.updateReport(eq(999L), any(ResearchReport.class)))
                    .thenThrow(new ResourceNotFoundException("Report", 999L));

            var request = createValidRequest();

            mockMvc.perform(put("/api/reports/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/reports/{id}")
    class DeleteReport {

        @Test
        @DisplayName("Gibt 204 zurück bei erfolgreichem Löschen")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(manageReportUseCase).deleteReport(1L);

            mockMvc.perform(delete("/api/reports/1"))
                    .andExpect(status().isNoContent());

            verify(manageReportUseCase).deleteReport(1L);
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn Report nicht existiert")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Report", 999L))
                    .when(manageReportUseCase).deleteReport(999L);

            mockMvc.perform(delete("/api/reports/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
