package com.research.portal.adapter.in.web.controller;

import com.research.portal.application.service.DataValidationResult;
import com.research.portal.application.service.ScheduledTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer {@link ValidationController}.
 * Testet HTTP-Endpunkte mit MockMvc.
 *
 * Verwendet @WebMvcTest, welches nur den Controller-Slice laedt.
 * Die @Scheduled-Annotationen im ScheduledTaskService werden nicht aktiviert,
 * da SchedulingConfig nicht Teil des WebMvc-Slices ist.
 */
@WebMvcTest(ValidationController.class)
@DisplayName("ValidationController")
class ValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduledTaskService scheduledTaskService;

    /**
     * Erstellt ein Beispiel-Validierungsergebnis mit Problemen.
     */
    private DataValidationResult createResultWithIssues() {
        return new DataValidationResult(
                2, 1, 3, 1,
                LocalDateTime.of(2026, 2, 23, 14, 30, 0)
        );
    }

    /**
     * Erstellt ein Beispiel-Validierungsergebnis ohne Probleme.
     */
    private DataValidationResult createCleanResult() {
        return new DataValidationResult(
                0, 0, 0, 0,
                LocalDateTime.of(2026, 2, 23, 14, 30, 0)
        );
    }

    @Nested
    @DisplayName("GET /api/validation")
    class GetLastResult {

        @Test
        @DisplayName("Gibt 200 zurueck")
        void shouldReturn200() throws Exception {
            when(scheduledTaskService.getLastValidationResult()).thenReturn(createCleanResult());
            when(scheduledTaskService.getLastValidationRun())
                    .thenReturn(LocalDateTime.of(2026, 2, 23, 14, 30, 0));

            mockMvc.perform(get("/api/validation"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Gibt Standardwerte zurueck wenn noch kein Lauf stattfand")
        void shouldReturnDefaultsWhenNoRunYet() throws Exception {
            when(scheduledTaskService.getLastValidationResult()).thenReturn(null);
            when(scheduledTaskService.getLastValidationRun()).thenReturn(null);

            mockMvc.perform(get("/api/validation"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orphanedReports").value(0))
                    .andExpect(jsonPath("$.invalidSecurityRefs").value(0))
                    .andExpect(jsonPath("$.negativeMarketCaps").value(0))
                    .andExpect(jsonPath("$.invalidAccuracies").value(0))
                    .andExpect(jsonPath("$.totalIssues").value(0))
                    .andExpect(jsonPath("$.hasIssues").value(false));
        }

        @Test
        @DisplayName("Response enthaelt alle Felder (orphanedReports, invalidSecurityRefs, etc.)")
        void shouldContainAllFields() throws Exception {
            when(scheduledTaskService.getLastValidationResult()).thenReturn(createResultWithIssues());
            when(scheduledTaskService.getLastValidationRun())
                    .thenReturn(LocalDateTime.of(2026, 2, 23, 14, 30, 0));

            mockMvc.perform(get("/api/validation"))
                    .andExpect(jsonPath("$.orphanedReports").value(2))
                    .andExpect(jsonPath("$.invalidSecurityRefs").value(1))
                    .andExpect(jsonPath("$.negativeMarketCaps").value(3))
                    .andExpect(jsonPath("$.invalidAccuracies").value(1))
                    .andExpect(jsonPath("$.lastRunAt").isString())
                    .andExpect(jsonPath("$.nextRunAt").isString());
        }

        @Test
        @DisplayName("Response enthaelt hasIssues und totalIssues")
        void shouldContainHasIssuesAndTotalIssues() throws Exception {
            when(scheduledTaskService.getLastValidationResult()).thenReturn(createResultWithIssues());
            when(scheduledTaskService.getLastValidationRun())
                    .thenReturn(LocalDateTime.of(2026, 2, 23, 14, 30, 0));

            mockMvc.perform(get("/api/validation"))
                    .andExpect(jsonPath("$.hasIssues").value(true))
                    .andExpect(jsonPath("$.totalIssues").value(7));
        }
    }

    @Nested
    @DisplayName("POST /api/validation/run")
    class RunValidation {

        @Test
        @DisplayName("Gibt 200 mit Ergebnis zurueck")
        void shouldReturn200WithResult() throws Exception {
            when(scheduledTaskService.runValidation()).thenReturn(createCleanResult());

            mockMvc.perform(post("/api/validation/run"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orphanedReports").value(0))
                    .andExpect(jsonPath("$.totalIssues").value(0))
                    .andExpect(jsonPath("$.hasIssues").value(false));
        }

        @Test
        @DisplayName("Ruft runValidation() auf dem Service auf")
        void shouldCallRunValidationOnService() throws Exception {
            when(scheduledTaskService.runValidation()).thenReturn(createCleanResult());

            mockMvc.perform(post("/api/validation/run"));

            verify(scheduledTaskService).runValidation();
        }

        @Test
        @DisplayName("Response enthaelt alle Felder bei Problemen")
        void shouldContainAllFieldsWithIssues() throws Exception {
            when(scheduledTaskService.runValidation()).thenReturn(createResultWithIssues());

            mockMvc.perform(post("/api/validation/run"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orphanedReports").value(2))
                    .andExpect(jsonPath("$.invalidSecurityRefs").value(1))
                    .andExpect(jsonPath("$.negativeMarketCaps").value(3))
                    .andExpect(jsonPath("$.invalidAccuracies").value(1))
                    .andExpect(jsonPath("$.totalIssues").value(7))
                    .andExpect(jsonPath("$.hasIssues").value(true))
                    .andExpect(jsonPath("$.lastRunAt").isString())
                    .andExpect(jsonPath("$.nextRunAt").isString());
        }
    }
}
