package com.research.portal.application.service;

import com.research.portal.domain.model.AssetClass;
import com.research.portal.domain.model.Security;
import com.research.portal.domain.port.out.SecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Tests für den SecurityService.
 * Testet Wertschriften-Abfragen mit gemocktem SecurityRepository.
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private SecurityRepository securityRepository;

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(securityRepository);
    }

    // Hilfsmethode: Erstellt eine Test-Wertschrift
    private Security createTestSecurity(Long id, String ticker, String name) {
        return new Security(
                id, ticker, "CH0012345678", name,
                AssetClass.EQUITY, "Consumer Staples", "Food Products",
                "SIX", "CHF", new BigDecimal("300000000000")
        );
    }

    @Nested
    @DisplayName("getAllSecurities()")
    class GetAllSecurities {

        @Test
        @DisplayName("Gibt alle Wertschriften zurück")
        void shouldReturnAllSecurities() {
            var securities = List.of(
                    createTestSecurity(1L, "NESN", "Nestlé SA"),
                    createTestSecurity(2L, "NOVN", "Novartis AG"),
                    createTestSecurity(3L, "UBSG", "UBS Group AG")
            );
            when(securityRepository.findAll()).thenReturn(securities);

            var result = securityService.getAllSecurities();

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getTicker()).isEqualTo("NESN");
            assertThat(result.get(1).getTicker()).isEqualTo("NOVN");
            assertThat(result.get(2).getTicker()).isEqualTo("UBSG");
            verify(securityRepository).findAll();
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Wertschriften existieren")
        void shouldReturnEmptyListWhenNoSecurities() {
            when(securityRepository.findAll()).thenReturn(List.of());

            var result = securityService.getAllSecurities();

            assertThat(result).isEmpty();
            verify(securityRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getSecurityById()")
    class GetSecurityById {

        @Test
        @DisplayName("Gibt Wertschrift zurück wenn ID existiert")
        void shouldReturnSecurityWhenFound() {
            var security = createTestSecurity(1L, "NESN", "Nestlé SA");
            when(securityRepository.findById(1L)).thenReturn(Optional.of(security));

            var result = securityService.getSecurityById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getTicker()).isEqualTo("NESN");
            assertThat(result.get().getName()).isEqualTo("Nestlé SA");
            assertThat(result.get().getAssetClass()).isEqualTo(AssetClass.EQUITY);
            verify(securityRepository).findById(1L);
        }

        @Test
        @DisplayName("Gibt leeres Optional zurück wenn ID nicht existiert")
        void shouldReturnEmptyWhenNotFound() {
            when(securityRepository.findById(999L)).thenReturn(Optional.empty());

            var result = securityService.getSecurityById(999L);

            assertThat(result).isEmpty();
            verify(securityRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("getSecurityByTicker()")
    class GetSecurityByTicker {

        @Test
        @DisplayName("Gibt Wertschrift zurück wenn Ticker existiert")
        void shouldReturnSecurityWhenTickerFound() {
            var security = createTestSecurity(1L, "NESN", "Nestlé SA");
            when(securityRepository.findByTicker("NESN")).thenReturn(Optional.of(security));

            var result = securityService.getSecurityByTicker("NESN");

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Nestlé SA");
            verify(securityRepository).findByTicker("NESN");
        }

        @Test
        @DisplayName("Gibt leeres Optional zurück wenn Ticker nicht existiert")
        void shouldReturnEmptyWhenTickerNotFound() {
            when(securityRepository.findByTicker("UNKNOWN")).thenReturn(Optional.empty());

            var result = securityService.getSecurityByTicker("UNKNOWN");

            assertThat(result).isEmpty();
            verify(securityRepository).findByTicker("UNKNOWN");
        }
    }
}
