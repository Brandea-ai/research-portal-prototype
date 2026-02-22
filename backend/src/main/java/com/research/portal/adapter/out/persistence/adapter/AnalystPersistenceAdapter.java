package com.research.portal.adapter.out.persistence.adapter;

import com.research.portal.adapter.out.persistence.mapper.AnalystPersistenceMapper;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.domain.model.Analyst;
import com.research.portal.domain.port.out.AnalystRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AnalystPersistenceAdapter implements AnalystRepository {

    private final JpaAnalystRepository jpaRepository;
    private final AnalystPersistenceMapper mapper;

    public AnalystPersistenceAdapter(JpaAnalystRepository jpaRepository,
                                     AnalystPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Analyst> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Analyst> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}
