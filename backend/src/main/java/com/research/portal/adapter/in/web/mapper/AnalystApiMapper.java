package com.research.portal.adapter.in.web.mapper;

import com.research.portal.adapter.in.web.dto.AnalystDto;
import com.research.portal.domain.model.Analyst;
import org.springframework.stereotype.Component;

@Component
public class AnalystApiMapper {

    public AnalystDto toDto(Analyst domain) {
        AnalystDto dto = new AnalystDto();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setTitle(domain.getTitle());
        dto.setDepartment(domain.getDepartment());
        dto.setEmail(domain.getEmail());
        dto.setCoverageUniverse(domain.getCoverageUniverse());
        dto.setStarRating(domain.getStarRating());
        dto.setAccuracy12m(domain.getAccuracy12m());
        return dto;
    }
}
