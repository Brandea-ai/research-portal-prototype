package com.research.portal.adapter.in.web.mapper;

import com.research.portal.domain.model.Analyst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für den AnalystApiMapper.
 * Prüft die korrekte Konvertierung von Analyst Domain zu AnalystDto.
 */
class AnalystApiMapperTest {

    private AnalystApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AnalystApiMapper();
    }

    @Test
    @DisplayName("Konvertiert alle Felder korrekt von Domain zu DTO")
    void shouldMapAllFieldsCorrectly() {
        var domain = new Analyst(
                1L, "Anna Meier", "Senior Equity Analyst", "Research",
                "anna.meier@bank.ch",
                List.of("NESN", "NOVN", "ROG", "UBSG"),
                5, 87.3
        );

        var dto = mapper.toDto(domain);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Anna Meier");
        assertThat(dto.getTitle()).isEqualTo("Senior Equity Analyst");
        assertThat(dto.getDepartment()).isEqualTo("Research");
        assertThat(dto.getEmail()).isEqualTo("anna.meier@bank.ch");
        assertThat(dto.getCoverageUniverse()).containsExactly("NESN", "NOVN", "ROG", "UBSG");
        assertThat(dto.getStarRating()).isEqualTo(5);
        assertThat(dto.getAccuracy12m()).isEqualTo(87.3);
    }

    @Test
    @DisplayName("Konvertiert Analyst mit leerer Coverage-Liste")
    void shouldHandleEmptyCoverageUniverse() {
        var domain = new Analyst(
                1L, "Neuer Analyst", "Junior Analyst", "Research",
                "neu@bank.ch", List.of(), 1, 0.0
        );

        var dto = mapper.toDto(domain);

        assertThat(dto.getCoverageUniverse()).isEmpty();
        assertThat(dto.getStarRating()).isEqualTo(1);
        assertThat(dto.getAccuracy12m()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Konvertiert Analyst mit null Coverage-Liste")
    void shouldHandleNullCoverageUniverse() {
        var domain = new Analyst();
        domain.setId(1L);
        domain.setName("Test");
        domain.setCoverageUniverse(null);

        var dto = mapper.toDto(domain);

        assertThat(dto.getCoverageUniverse()).isNull();
    }

    @Test
    @DisplayName("Behält numerische Werte exakt bei")
    void shouldPreserveNumericValues() {
        var domain = new Analyst(
                1L, "Test", "Title", "Dept", "test@bank.ch",
                List.of(), 3, 65.789
        );

        var dto = mapper.toDto(domain);

        assertThat(dto.getStarRating()).isEqualTo(3);
        assertThat(dto.getAccuracy12m()).isEqualTo(65.789);
    }
}
