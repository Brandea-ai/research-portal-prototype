package com.research.portal.adapter.out.persistence.adapter;

import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import com.research.portal.adapter.out.persistence.mapper.ReportPersistenceMapper;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für den ReportPersistenceAdapter.
 * Testet die Brücke zwischen Domain-Port und JPA Repository.
 */
@ExtendWith(MockitoExtension.class)
class ReportPersistenceAdapterTest {

    @Mock
    private JpaReportRepository jpaRepository;

    @Mock
    private ReportPersistenceMapper mapper;

    private ReportPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ReportPersistenceAdapter(jpaRepository, mapper);
    }

    // Hilfsmethode: Erstellt eine Test-Entity
    private ResearchReportEntity createTestEntity(Long id) {
        ResearchReportEntity entity = new ResearchReportEntity();
        entity.setId(id);
        entity.setAnalystId(1L);
        entity.setSecurityId(1L);
        entity.setPublishedAt(LocalDateTime.of(2026, 2, 22, 10, 0));
        entity.setReportType("UPDATE");
        entity.setTitle("Test Report");
        entity.setExecutiveSummary("Zusammenfassung");
        entity.setRating("BUY");
        entity.setRatingChanged(false);
        entity.setTargetPrice(new BigDecimal("120.00"));
        return entity;
    }

    // Hilfsmethode: Erstellt ein Domain-Objekt
    private ResearchReport createTestDomain(Long id) {
        ResearchReport report = new ResearchReport();
        report.setId(id);
        report.setAnalystId(1L);
        report.setSecurityId(1L);
        report.setPublishedAt(LocalDateTime.of(2026, 2, 22, 10, 0));
        report.setReportType(ReportType.UPDATE);
        report.setTitle("Test Report");
        report.setExecutiveSummary("Zusammenfassung");
        report.setRating(Rating.BUY);
        report.setRatingChanged(false);
        report.setTargetPrice(new BigDecimal("120.00"));
        return report;
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Gibt alle Reports als Domain-Objekte zurück")
        void shouldReturnAllReportsAsDomain() {
            var entity1 = createTestEntity(1L);
            var entity2 = createTestEntity(2L);
            var domain1 = createTestDomain(1L);
            var domain2 = createTestDomain(2L);

            when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
            when(mapper.toDomain(entity1)).thenReturn(domain1);
            when(mapper.toDomain(entity2)).thenReturn(domain2);

            var result = adapter.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
            verify(jpaRepository).findAll();
            verify(mapper, times(2)).toDomain(any());
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Entities existieren")
        void shouldReturnEmptyList() {
            when(jpaRepository.findAll()).thenReturn(List.of());

            var result = adapter.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Gibt Domain-Objekt zurück wenn Entity existiert")
        void shouldReturnDomainWhenFound() {
            var entity = createTestEntity(1L);
            var domain = createTestDomain(1L);
            when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Gibt leeres Optional zurück wenn Entity nicht existiert")
        void shouldReturnEmptyWhenNotFound() {
            when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

            var result = adapter.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAnalystId()")
    class FindByAnalystId {

        @Test
        @DisplayName("Gibt Reports eines Analysten zurück")
        void shouldReturnReportsForAnalyst() {
            var entity = createTestEntity(1L);
            var domain = createTestDomain(1L);
            when(jpaRepository.findByAnalystId(1L)).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.findByAnalystId(1L);

            assertThat(result).hasSize(1);
            verify(jpaRepository).findByAnalystId(1L);
        }
    }

    @Nested
    @DisplayName("findBySecurityId()")
    class FindBySecurityId {

        @Test
        @DisplayName("Gibt Reports einer Wertschrift zurück")
        void shouldReturnReportsForSecurity() {
            var entity = createTestEntity(1L);
            var domain = createTestDomain(1L);
            when(jpaRepository.findBySecurityId(1L)).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.findBySecurityId(1L);

            assertThat(result).hasSize(1);
            verify(jpaRepository).findBySecurityId(1L);
        }
    }

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("Speichert Domain-Objekt und gibt gespeichertes zurück")
        void shouldSaveAndReturnDomain() {
            var inputDomain = createTestDomain(null);
            var entity = createTestEntity(null);
            var savedEntity = createTestEntity(1L);
            var savedDomain = createTestDomain(1L);

            when(mapper.toEntity(inputDomain)).thenReturn(entity);
            when(jpaRepository.save(entity)).thenReturn(savedEntity);
            when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

            var result = adapter.save(inputDomain);

            assertThat(result.getId()).isEqualTo(1L);
            verify(mapper).toEntity(inputDomain);
            verify(jpaRepository).save(entity);
            verify(mapper).toDomain(savedEntity);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteById {

        @Test
        @DisplayName("Delegiert delete an JPA Repository")
        void shouldDelegateDelete() {
            adapter.deleteById(1L);

            verify(jpaRepository).deleteById(1L);
        }
    }
}
