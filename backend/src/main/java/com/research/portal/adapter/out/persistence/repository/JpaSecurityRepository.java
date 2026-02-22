package com.research.portal.adapter.out.persistence.repository;

import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSecurityRepository extends JpaRepository<SecurityEntity, Long> {

    Optional<SecurityEntity> findByTicker(String ticker);
}
