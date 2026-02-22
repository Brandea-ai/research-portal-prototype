package com.research.portal.adapter.out.persistence.repository;

import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReportRepository extends JpaRepository<ResearchReportEntity, Long> {

    List<ResearchReportEntity> findByAnalystId(Long analystId);

    List<ResearchReportEntity> findBySecurityId(Long securityId);
}
