package com.research.portal.adapter.in.web.mapper;

import com.research.portal.domain.model.AssetClass;
import com.research.portal.domain.model.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für den SecurityApiMapper.
 * Prüft die korrekte Konvertierung von Security Domain zu SecurityDto.
 */
class SecurityApiMapperTest {

    private SecurityApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SecurityApiMapper();
    }

    @Test
    @DisplayName("Konvertiert alle Felder korrekt von Domain zu DTO")
    void shouldMapAllFieldsCorrectly() {
        var domain = new Security(
                1L, "NESN", "CH0038863350", "Nestlé SA",
                AssetClass.EQUITY, "Consumer Staples", "Food Products",
                "SIX", "CHF", new BigDecimal("300000000000")
        );

        var dto = mapper.toDto(domain);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTicker()).isEqualTo("NESN");
        assertThat(dto.getIsin()).isEqualTo("CH0038863350");
        assertThat(dto.getName()).isEqualTo("Nestlé SA");
        assertThat(dto.getAssetClass()).isEqualTo("EQUITY");
        assertThat(dto.getSector()).isEqualTo("Consumer Staples");
        assertThat(dto.getIndustry()).isEqualTo("Food Products");
        assertThat(dto.getExchange()).isEqualTo("SIX");
        assertThat(dto.getCurrency()).isEqualTo("CHF");
        assertThat(dto.getMarketCap()).isEqualByComparingTo(new BigDecimal("300000000000"));
    }

    @Test
    @DisplayName("Konvertiert verschiedene AssetClasses korrekt")
    void shouldMapDifferentAssetClasses() {
        var equity = new Security(1L, "NESN", "CH001", "Nestlé",
                AssetClass.EQUITY, "CS", "FP", "SIX", "CHF", BigDecimal.ZERO);
        var fixedIncome = new Security(2L, "BOND", "CH002", "Swiss Gov Bond",
                AssetClass.FIXED_INCOME, "Gov", "Bond", "SIX", "CHF", BigDecimal.ZERO);
        var derivatives = new Security(3L, "OPT", "CH003", "Option",
                AssetClass.DERIVATIVES, "Deriv", "Options", "EUREX", "CHF", BigDecimal.ZERO);
        var macro = new Security(4L, "MACRO", "CH004", "Macro Index",
                AssetClass.MACRO, "Index", "Broad", "SIX", "CHF", BigDecimal.ZERO);

        assertThat(mapper.toDto(equity).getAssetClass()).isEqualTo("EQUITY");
        assertThat(mapper.toDto(fixedIncome).getAssetClass()).isEqualTo("FIXED_INCOME");
        assertThat(mapper.toDto(derivatives).getAssetClass()).isEqualTo("DERIVATIVES");
        assertThat(mapper.toDto(macro).getAssetClass()).isEqualTo("MACRO");
    }

    @Test
    @DisplayName("Konvertiert Wertschrift mit null-Feldern korrekt")
    void shouldHandleNullFields() {
        var domain = new Security();
        domain.setId(1L);
        domain.setTicker("TEST");
        domain.setName("Test Security");
        domain.setAssetClass(AssetClass.EQUITY);
        // sector, industry, exchange, currency, marketCap bleiben null

        var dto = mapper.toDto(domain);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTicker()).isEqualTo("TEST");
        assertThat(dto.getSector()).isNull();
        assertThat(dto.getIndustry()).isNull();
        assertThat(dto.getMarketCap()).isNull();
    }
}
