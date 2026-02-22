package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.in.web.mapper.AnalystApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.model.Analyst;
import com.research.portal.domain.port.in.GetAnalystsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests für AnalystController.
 * Testet Analysten-Endpoints mit MockMvc.
 */
@WebMvcTest(AnalystController.class)
@Import({AnalystApiMapper.class, GlobalExceptionHandler.class})
class AnalystControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAnalystsUseCase getAnalystsUseCase;

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
    @DisplayName("GET /api/analysts")
    class GetAllAnalysts {

        @Test
        @DisplayName("Gibt 200 und Liste aller Analysten zurück")
        void shouldReturnAllAnalysts() throws Exception {
            var analysts = List.of(
                    createTestAnalyst(1L, "Anna Meier"),
                    createTestAnalyst(2L, "Thomas Mueller")
            );
            when(getAnalystsUseCase.getAllAnalysts()).thenReturn(analysts);

            mockMvc.perform(get("/api/analysts"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Anna Meier"))
                    .andExpect(jsonPath("$[0].title").value("Senior Equity Analyst"))
                    .andExpect(jsonPath("$[0].department").value("Research"))
                    .andExpect(jsonPath("$[0].starRating").value(4))
                    .andExpect(jsonPath("$[0].accuracy12m").value(82.5))
                    .andExpect(jsonPath("$[0].coverageUniverse", hasSize(3)))
                    .andExpect(jsonPath("$[1].name").value("Thomas Mueller"));
        }

        @Test
        @DisplayName("Gibt 200 und leere Liste zurück")
        void shouldReturnEmptyList() throws Exception {
            when(getAnalystsUseCase.getAllAnalysts()).thenReturn(List.of());

            mockMvc.perform(get("/api/analysts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/analysts/{id}")
    class GetAnalystById {

        @Test
        @DisplayName("Gibt 200 und Analyst zurück wenn ID existiert")
        void shouldReturnAnalystWhenFound() throws Exception {
            var analyst = createTestAnalyst(1L, "Anna Meier");
            when(getAnalystsUseCase.getAnalystById(1L)).thenReturn(Optional.of(analyst));

            mockMvc.perform(get("/api/analysts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Anna Meier"))
                    .andExpect(jsonPath("$.title").value("Senior Equity Analyst"))
                    .andExpect(jsonPath("$.department").value("Research"))
                    .andExpect(jsonPath("$.email").value("anna.meier@bank.ch"))
                    .andExpect(jsonPath("$.starRating").value(4))
                    .andExpect(jsonPath("$.accuracy12m").value(82.5))
                    .andExpect(jsonPath("$.coverageUniverse[0]").value("NESN"))
                    .andExpect(jsonPath("$.coverageUniverse[1]").value("NOVN"))
                    .andExpect(jsonPath("$.coverageUniverse[2]").value("ROG"));
        }

        @Test
        @DisplayName("Gibt 404 zurück wenn ID nicht existiert")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getAnalystsUseCase.getAnalystById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/analysts/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Analyst mit ID 999 nicht gefunden"));
        }
    }
}
