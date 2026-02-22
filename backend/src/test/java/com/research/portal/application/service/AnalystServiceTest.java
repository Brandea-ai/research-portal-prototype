package com.research.portal.application.service;

import com.research.portal.domain.model.Analyst;
import com.research.portal.domain.port.out.AnalystRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit Tests für den AnalystService.
 * Testet Analysten-Abfragen mit gemocktem AnalystRepository.
 */
@ExtendWith(MockitoExtension.class)
class AnalystServiceTest {

    @Mock
    private AnalystRepository analystRepository;

    private AnalystService analystService;

    @BeforeEach
    void setUp() {
        analystService = new AnalystService(analystRepository);
    }

    // Hilfsmethode: Erstellt einen Test-Analysten
    private Analyst createTestAnalyst(Long id, String name) {
        return new Analyst(
                id, name, "Senior Equity Analyst", "Research",
                name.toLowerCase().replace(" ", ".") + "@bank.ch",
                List.of("NESN", "NOVN", "ROG"),
                4, 82.5
        );
    }

    @Nested
    @DisplayName("getAllAnalysts()")
    class GetAllAnalysts {

        @Test
        @DisplayName("Gibt alle Analysten zurück")
        void shouldReturnAllAnalysts() {
            var analysts = List.of(
                    createTestAnalyst(1L, "Anna Meier"),
                    createTestAnalyst(2L, "Thomas Müller")
            );
            when(analystRepository.findAll()).thenReturn(analysts);

            var result = analystService.getAllAnalysts();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Anna Meier");
            assertThat(result.get(1).getName()).isEqualTo("Thomas Müller");
            verify(analystRepository).findAll();
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Analysten existieren")
        void shouldReturnEmptyListWhenNoAnalysts() {
            when(analystRepository.findAll()).thenReturn(List.of());

            var result = analystService.getAllAnalysts();

            assertThat(result).isEmpty();
            verify(analystRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getAnalystById()")
    class GetAnalystById {

        @Test
        @DisplayName("Gibt Analyst zurück wenn ID existiert")
        void shouldReturnAnalystWhenFound() {
            var analyst = createTestAnalyst(1L, "Anna Meier");
            when(analystRepository.findById(1L)).thenReturn(Optional.of(analyst));

            var result = analystService.getAnalystById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Anna Meier");
            assertThat(result.get().getDepartment()).isEqualTo("Research");
            assertThat(result.get().getStarRating()).isEqualTo(4);
            assertThat(result.get().getAccuracy12m()).isEqualTo(82.5);
            verify(analystRepository).findById(1L);
        }

        @Test
        @DisplayName("Gibt leeres Optional zurück wenn ID nicht existiert")
        void shouldReturnEmptyWhenNotFound() {
            when(analystRepository.findById(999L)).thenReturn(Optional.empty());

            var result = analystService.getAnalystById(999L);

            assertThat(result).isEmpty();
            verify(analystRepository).findById(999L);
        }
    }
}
