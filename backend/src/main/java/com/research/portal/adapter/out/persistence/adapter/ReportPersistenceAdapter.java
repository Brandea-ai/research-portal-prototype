package com.research.portal.adapter.out.persistence.adapter;

import com.research.portal.adapter.out.persistence.mapper.ReportPersistenceMapper;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.domain.model.ResearchReport;
import com.research.portal.domain.port.out.ReportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReportPersistenceAdapter implements ReportRepository {

    private final JpaReportRepository jpaRepository;
    private final ReportPersistenceMapper mapper;

    public ReportPersistenceAdapter(JpaReportRepository jpaRepository,
                                     ReportPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ResearchReport> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ResearchReport> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ResearchReport> findByAnalystId(Long analystId) {
        return jpaRepository.findByAnalystId(analystId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ResearchReport> findBySecurityId(Long securityId) {
        return jpaRepository.findBySecurityId(securityId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public ResearchReport save(ResearchReport report) {
        var entity = mapper.toEntity(report);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
