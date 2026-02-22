package com.research.portal.adapter.out.persistence.adapter;

import com.research.portal.adapter.out.persistence.mapper.SecurityPersistenceMapper;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import com.research.portal.domain.model.Security;
import com.research.portal.domain.port.out.SecurityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SecurityPersistenceAdapter implements SecurityRepository {

    private final JpaSecurityRepository jpaRepository;
    private final SecurityPersistenceMapper mapper;

    public SecurityPersistenceAdapter(JpaSecurityRepository jpaRepository,
                                       SecurityPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Security> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Security> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Security> findByTicker(String ticker) {
        return jpaRepository.findByTicker(ticker)
                .map(mapper::toDomain);
    }
}
