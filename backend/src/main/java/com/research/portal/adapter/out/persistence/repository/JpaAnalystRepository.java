package com.research.portal.adapter.out.persistence.repository;

import com.research.portal.adapter.out.persistence.entity.AnalystEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAnalystRepository extends JpaRepository<AnalystEntity, Long> {
}
