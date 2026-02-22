package com.research.portal.adapter.out.persistence.mapper;

import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import com.research.portal.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ReportPersistenceMapper {

    public ResearchReport toDomain(ResearchReportEntity entity) {
        ResearchReport report = new ResearchReport();
        report.setId(entity.getId());
        report.setAnalystId(entity.getAnalystId());
        report.setSecurityId(entity.getSecurityId());
        report.setPublishedAt(entity.getPublishedAt());
        report.setReportType(ReportType.valueOf(entity.getReportType()));
        report.setTitle(entity.getTitle());
        report.setExecutiveSummary(entity.getExecutiveSummary());
        report.setFullText(entity.getFullText());
        report.setRating(Rating.valueOf(entity.getRating()));
        report.setPreviousRating(
                entity.getPreviousRating() != null
                        ? Rating.valueOf(entity.getPreviousRating())
                        : null
        );
        report.setRatingChanged(entity.isRatingChanged());
        report.setTargetPrice(entity.getTargetPrice());
        report.setPreviousTarget(entity.getPreviousTarget());
        report.setCurrentPrice(entity.getCurrentPrice());
        report.setImpliedUpside(entity.getImpliedUpside());
        report.setRiskLevel(
                entity.getRiskLevel() != null
                        ? RiskLevel.valueOf(entity.getRiskLevel())
                        : null
        );
        report.setInvestmentCatalysts(splitString(entity.getInvestmentCatalysts()));
        report.setKeyRisks(splitString(entity.getKeyRisks()));
        report.setTags(splitString(entity.getTags()));
        return report;
    }

    public ResearchReportEntity toEntity(ResearchReport domain) {
        ResearchReportEntity entity = new ResearchReportEntity();
        entity.setId(domain.getId());
        entity.setAnalystId(domain.getAnalystId());
        entity.setSecurityId(domain.getSecurityId());
        entity.setPublishedAt(domain.getPublishedAt());
        entity.setReportType(domain.getReportType().name());
        entity.setTitle(domain.getTitle());
        entity.setExecutiveSummary(domain.getExecutiveSummary());
        entity.setFullText(domain.getFullText());
        entity.setRating(domain.getRating().name());
        entity.setPreviousRating(
                domain.getPreviousRating() != null
                        ? domain.getPreviousRating().name()
                        : null
        );
        entity.setRatingChanged(domain.isRatingChanged());
        entity.setTargetPrice(domain.getTargetPrice());
        entity.setPreviousTarget(domain.getPreviousTarget());
        entity.setCurrentPrice(domain.getCurrentPrice());
        entity.setImpliedUpside(domain.getImpliedUpside());
        entity.setRiskLevel(
                domain.getRiskLevel() != null
                        ? domain.getRiskLevel().name()
                        : null
        );
        entity.setInvestmentCatalysts(joinList(domain.getInvestmentCatalysts()));
        entity.setKeyRisks(joinList(domain.getKeyRisks()));
        entity.setTags(joinList(domain.getTags()));
        return entity;
    }

    private List<String> splitString(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split("\\|"));
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join("|", list);
    }
}
