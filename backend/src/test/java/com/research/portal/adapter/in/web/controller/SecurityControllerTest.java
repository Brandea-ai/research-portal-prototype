package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.in.web.mapper.SecurityApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.model.AssetClass;
import com.research.portal.domain.model.Security;
import com.research.portal.domain.port.in.GetSecuritiesUseCase;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests für SecurityController.
 * Testet Wertschriften-Endpoints mit MockMvc.
 */
@WebMvcTest(SecurityController.class)
@Import({SecurityApiMapper.class, GlobalExceptionHandler.class})
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetSecuritiesUseCase getSecuritiesUseCase;

    // Hilfsmethode: Erstellt eine Test-Wertschrift
    private Security createTestSecurity(Long id, String ticker, String name) {
        return new Security(
                id, ticker, "CH0012345678", name,
                AssetClass.EQUITY, "Consumer Staples", "Food Products",
                "SIX", "CHF", new BigDecimal("300000000000")
        );
    }

    @Nested
    @DisplayName("GET /api/securities")
    class GetAllSecurities {

        @Test
        @DisplayName("Gibt 200 und Liste aller Wertschriften zurück")
        void shouldReturnAllSecurities() throws Exception {
            var securities = List.of(
                    createTestSecurity(1L, "NESN", "Nestlé SA"),
                    createTestSecurity(2L, "NOVN", "Novartis AG")
            );
            when(getSecuritiesUseCase.getAllSecurities()).thenReturn(securities);

            mockMvc.perform(get("/api/securities"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].ticker").value("NESN"))
                    .andExpect(jsonPath("$[0].name").value("Nestlé SA"))
                    .andExpect(jsonPath("$[0].assetClass").value("EQUITY"))
                    .andExpect(jsonPath("$[0].currency").value("CHF"))
                    .andExpect(jsonPath("$[1].ticker").value("NOVN"));
        }

        @Test
        @DisplayName("Gibt 200 und leere Liste zurück")
        void shouldReturnEmptyList() throws Exception {
            when(getSecuritiesUseCase.getAllSecurities()).thenReturn(List.of());

            mockMvc.perform(get("/api/securities"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/securities/{id}")
    class GetSecurityById {

        @Test
        @DisplayName("Gibt 200 und Wertschrift zurück wenn ID existiert")
        void shouldReturnSecurityWhenFound() throws Exception {
            var security = createTestSecurity(1L, "NESN", "Nestlé SA");
            when(getSecuritiesUseCase.getSecurityById(1L)).thenReturn(Optional.of(security));

            mockMvc.perform(get("/api/securities/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.ticker").value("NESN"))
                    .andExpect(jsonPath("$.name").value("Nestlé SA"))
                    .andExpect(jsonPath("$.isin").value("CH0012345678"))
                    .andExpect(jsonPath("$.assetClass").value("EQUITY"))
                    .andExpect(jsonPath("$.sector").value("Consumer Staples"))
                    .andExpect(jsonPath("$.exchange").value("SIX"))
                    .andExpect(jsonPath("$.currency").value("CHF"));
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn ID nicht existiert")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getSecuritiesUseCase.getSecurityById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/securities/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Nested
    @DisplayName("GET /api/securities?ticker=")
    class GetSecurityByTicker {

        @Test
        @DisplayName("Gibt 200 und Wertschrift zurück wenn Ticker existiert")
        void shouldReturnSecurityWhenTickerFound() throws Exception {
            var security = createTestSecurity(1L, "NESN", "Nestlé SA");
            when(getSecuritiesUseCase.getSecurityByTicker("NESN")).thenReturn(Optional.of(security));

            mockMvc.perform(get("/api/securities").param("ticker", "NESN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ticker").value("NESN"))
                    .andExpect(jsonPath("$.name").value("Nestlé SA"));
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn Ticker nicht existiert")
        void shouldReturn404WhenTickerNotFound() throws Exception {
            when(getSecuritiesUseCase.getSecurityByTicker("UNKNOWN")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/securities").param("ticker", "UNKNOWN"))
                    .andExpect(status().isNotFound());
        }
    }
}
