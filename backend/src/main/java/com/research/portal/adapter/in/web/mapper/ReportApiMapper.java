package com.research.portal.adapter.in.web.mapper;

import com.research.portal.adapter.in.web.dto.CreateReportRequest;
import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportApiMapper {

    public ReportDto toDto(ResearchReport domain) {
        ReportDto dto = new ReportDto();
        dto.setId(domain.getId());
        dto.setAnalystId(domain.getAnalystId());
        dto.setSecurityId(domain.getSecurityId());
        dto.setPublishedAt(domain.getPublishedAt());
        dto.setReportType(domain.getReportType().name());
        dto.setTitle(domain.getTitle());
        dto.setExecutiveSummary(domain.getExecutiveSummary());
        dto.setFullText(domain.getFullText());
        dto.setRating(domain.getRating().name());
        dto.setPreviousRating(
                domain.getPreviousRating() != null
                        ? domain.getPreviousRating().name()
                        : null
        );
        dto.setRatingChanged(domain.isRatingChanged());
        dto.setTargetPrice(domain.getTargetPrice());
        dto.setPreviousTarget(domain.getPreviousTarget());
        dto.setCurrentPrice(domain.getCurrentPrice());
        dto.setImpliedUpside(domain.getImpliedUpside());
        dto.setRiskLevel(
                domain.getRiskLevel() != null
                        ? domain.getRiskLevel().name()
                        : null
        );
        dto.setInvestmentCatalysts(domain.getInvestmentCatalysts());
        dto.setKeyRisks(domain.getKeyRisks());
        dto.setTags(domain.getTags());
        return dto;
    }

    public ResearchReport toDomain(CreateReportRequest request) {
        ResearchReport report = new ResearchReport();
        report.setAnalystId(request.getAnalystId());
        report.setSecurityId(request.getSecurityId());
        report.setPublishedAt(LocalDateTime.now());
        report.setReportType(ReportType.valueOf(request.getReportType()));
        report.setTitle(request.getTitle());
        report.setExecutiveSummary(request.getExecutiveSummary());
        report.setFullText(request.getFullText());
        report.setRating(Rating.valueOf(request.getRating()));
        report.setPreviousRating(
                request.getPreviousRating() != null
                        ? Rating.valueOf(request.getPreviousRating())
                        : null
        );
        report.setRatingChanged(request.getPreviousRating() != null
                && !request.getRating().equals(request.getPreviousRating()));
        report.setTargetPrice(request.getTargetPrice());
        report.setPreviousTarget(request.getPreviousTarget());
        report.setCurrentPrice(request.getCurrentPrice());
        if (request.getCurrentPrice() != null && request.getTargetPrice() != null
                && request.getCurrentPrice().signum() > 0) {
            report.setImpliedUpside(
                    request.getTargetPrice()
                            .subtract(request.getCurrentPrice())
                            .multiply(java.math.BigDecimal.valueOf(100))
                            .divide(request.getCurrentPrice(), 2, java.math.RoundingMode.HALF_UP)
            );
        }
        report.setRiskLevel(
                request.getRiskLevel() != null
                        ? RiskLevel.valueOf(request.getRiskLevel())
                        : null
        );
        report.setInvestmentCatalysts(request.getInvestmentCatalysts());
        report.setKeyRisks(request.getKeyRisks());
        report.setTags(request.getTags());
        return report;
    }
}
