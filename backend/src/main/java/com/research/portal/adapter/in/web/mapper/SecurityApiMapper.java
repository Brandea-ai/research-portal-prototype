package com.research.portal.adapter.in.web.mapper;

import com.research.portal.adapter.in.web.dto.SecurityDto;
import com.research.portal.domain.model.Security;
import org.springframework.stereotype.Component;

@Component
public class SecurityApiMapper {

    public SecurityDto toDto(Security domain) {
        SecurityDto dto = new SecurityDto();
        dto.setId(domain.getId());
        dto.setTicker(domain.getTicker());
        dto.setIsin(domain.getIsin());
        dto.setName(domain.getName());
        dto.setAssetClass(domain.getAssetClass().name());
        dto.setSector(domain.getSector());
        dto.setIndustry(domain.getIndustry());
        dto.setExchange(domain.getExchange());
        dto.setCurrency(domain.getCurrency());
        dto.setMarketCap(domain.getMarketCap());
        return dto;
    }
}
