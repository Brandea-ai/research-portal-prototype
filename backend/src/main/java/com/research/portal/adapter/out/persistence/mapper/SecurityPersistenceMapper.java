package com.research.portal.adapter.out.persistence.mapper;

import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.domain.model.AssetClass;
import com.research.portal.domain.model.Security;
import org.springframework.stereotype.Component;

@Component
public class SecurityPersistenceMapper {

    public Security toDomain(SecurityEntity entity) {
        return new Security(
                entity.getId(),
                entity.getTicker(),
                entity.getIsin(),
                entity.getName(),
                AssetClass.valueOf(entity.getAssetClass()),
                entity.getSector(),
                entity.getIndustry(),
                entity.getExchange(),
                entity.getCurrency(),
                entity.getMarketCap()
        );
    }

    public SecurityEntity toEntity(Security domain) {
        SecurityEntity entity = new SecurityEntity();
        entity.setId(domain.getId());
        entity.setTicker(domain.getTicker());
        entity.setIsin(domain.getIsin());
        entity.setName(domain.getName());
        entity.setAssetClass(domain.getAssetClass().name());
        entity.setSector(domain.getSector());
        entity.setIndustry(domain.getIndustry());
        entity.setExchange(domain.getExchange());
        entity.setCurrency(domain.getCurrency());
        entity.setMarketCap(domain.getMarketCap());
        return entity;
    }
}
