package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.in.web.dto.SearchResponseDto;
import com.research.portal.adapter.in.web.dto.SearchResultDto;
import com.research.portal.application.service.SearchService;
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
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer SearchController.
 * Testet HTTP Endpoints mit MockMvc und gemocktem SearchService.
 */
@WebMvcTest(SearchController.class)
@Import(GlobalExceptionHandler.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    // === Hilfsmethoden ===

    private SearchResponseDto createSearchResponse(String query, List<SearchResultDto> results) {
        Map<String, Integer> resultsByType = new java.util.HashMap<>();
        for (SearchResultDto result : results) {
            resultsByType.merge(result.getType(), 1, Integer::sum);
        }
        return new SearchResponseDto(query, results.size(), results, resultsByType);
    }

    private SearchResultDto createResult(String type, Long id, String title, String subtitle, String highlight, double relevance) {
        return new SearchResultDto(type, id, title, subtitle, highlight, relevance);
    }

    @Nested
    @DisplayName("GET /api/search?q=...")
    class GlobalSearch {

        @Test
        @DisplayName("Gibt 200 und Ergebnisse zurück für gültige Suche")
        void shouldReturn200WithResults() throws Exception {
            var results = List.of(
                    createResult("SECURITY", 1L, "Nestlé SA", "NESN | Consumer Staples", "Nestlé SA", 1.0),
                    createResult("REPORT", 1L, "Nestlé SA - Initiation", "BUY | INITIATION", "Nestlé SA - Initiation of Coverage", 0.8)
            );
            var response = createSearchResponse("Nestlé", results);
            when(searchService.search(eq("Nestlé"), isNull(), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search").param("q", "Nestlé"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.query").value("Nestlé"))
                    .andExpect(jsonPath("$.totalResults").value(2))
                    .andExpect(jsonPath("$.results", hasSize(2)))
                    .andExpect(jsonPath("$.results[0].type").value("SECURITY"))
                    .andExpect(jsonPath("$.results[0].title").value("Nestlé SA"))
                    .andExpect(jsonPath("$.results[0].relevance").value(1.0))
                    .andExpect(jsonPath("$.results[1].type").value("REPORT"))
                    .andExpect(jsonPath("$.resultsByType.SECURITY").value(1))
                    .andExpect(jsonPath("$.resultsByType.REPORT").value(1));
        }

        @Test
        @DisplayName("Gibt 400 zurück ohne q-Parameter")
        void shouldReturn400WhenQueryMissing() throws Exception {
            mockMvc.perform(get("/api/search"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Response enthält query, totalResults, results und resultsByType")
        void shouldContainAllResponseFields() throws Exception {
            var response = new SearchResponseDto("test", 0, List.of(), Map.of());
            when(searchService.search(eq("test"), isNull(), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search").param("q", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.query").exists())
                    .andExpect(jsonPath("$.totalResults").exists())
                    .andExpect(jsonPath("$.results").exists())
                    .andExpect(jsonPath("$.resultsByType").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/search?q=...&type=...")
    class TypeFiltered {

        @Test
        @DisplayName("Typ-Filter REPORT wird an Service weitergegeben")
        void shouldPassTypeFilterToService() throws Exception {
            var results = List.of(
                    createResult("REPORT", 1L, "Test Report", "BUY | UPDATE", "Test", 0.8)
            );
            var response = createSearchResponse("test", results);
            when(searchService.search(eq("test"), eq("REPORT"), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search")
                            .param("q", "test")
                            .param("type", "REPORT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.results", hasSize(1)))
                    .andExpect(jsonPath("$.results[0].type").value("REPORT"));

            verify(searchService).search("test", "REPORT", 20);
        }

        @Test
        @DisplayName("Typ-Filter ANALYST wird an Service weitergegeben")
        void shouldPassAnalystTypeFilter() throws Exception {
            var results = List.of(
                    createResult("ANALYST", 1L, "Dr. Elena Fischer", "Senior Analyst | Equity", "Fischer", 0.8)
            );
            var response = createSearchResponse("Fischer", results);
            when(searchService.search(eq("Fischer"), eq("ANALYST"), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search")
                            .param("q", "Fischer")
                            .param("type", "ANALYST"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.results[0].type").value("ANALYST"));

            verify(searchService).search("Fischer", "ANALYST", 20);
        }
    }

    @Nested
    @DisplayName("GET /api/search?q=...&limit=...")
    class LimitParameter {

        @Test
        @DisplayName("Limit Parameter wird an Service weitergegeben")
        void shouldPassLimitToService() throws Exception {
            var response = new SearchResponseDto("test", 0, List.of(), Map.of());
            when(searchService.search(eq("test"), isNull(), eq(5))).thenReturn(response);

            mockMvc.perform(get("/api/search")
                            .param("q", "test")
                            .param("limit", "5"))
                    .andExpect(status().isOk());

            verify(searchService).search("test", null, 5);
        }

        @Test
        @DisplayName("Default Limit ist 20")
        void shouldUseDefaultLimitOf20() throws Exception {
            var response = new SearchResponseDto("test", 0, List.of(), Map.of());
            when(searchService.search(eq("test"), isNull(), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search").param("q", "test"))
                    .andExpect(status().isOk());

            verify(searchService).search("test", null, 20);
        }
    }

    @Nested
    @DisplayName("Leere Suchergebnisse")
    class EmptyResults {

        @Test
        @DisplayName("Gibt 200 mit leerer Liste zurück wenn nichts gefunden")
        void shouldReturn200WithEmptyResults() throws Exception {
            var response = new SearchResponseDto("xyz123", 0, List.of(), Map.of());
            when(searchService.search(eq("xyz123"), isNull(), eq(20))).thenReturn(response);

            mockMvc.perform(get("/api/search").param("q", "xyz123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalResults").value(0))
                    .andExpect(jsonPath("$.results", hasSize(0)));
        }
    }
}
