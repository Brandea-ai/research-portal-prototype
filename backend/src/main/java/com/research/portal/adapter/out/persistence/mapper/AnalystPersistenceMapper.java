package com.research.portal.adapter.out.persistence.mapper;

import com.research.portal.adapter.out.persistence.entity.AnalystEntity;
import com.research.portal.domain.model.Analyst;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Übersetzt zwischen JPA AnalystEntity und Domain Analyst.
 * Die Domain kennt keine JPA-Annotationen,
 * daher brauchen wir diesen Mapper als Brücke.
 */
@Component
public class AnalystPersistenceMapper {

    public Analyst toDomain(AnalystEntity entity) {
        List<String> coverage = entity.getCoverageUniverse() != null
                ? Arrays.asList(entity.getCoverageUniverse().split(","))
                : Collections.emptyList();

        return new Analyst(
                entity.getId(),
                entity.getName(),
                entity.getTitle(),
                entity.getDepartment(),
                entity.getEmail(),
                coverage,
                entity.getStarRating(),
                entity.getAccuracy12m()
        );
    }

    public AnalystEntity toEntity(Analyst domain) {
        AnalystEntity entity = new AnalystEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setTitle(domain.getTitle());
        entity.setDepartment(domain.getDepartment());
        entity.setEmail(domain.getEmail());
        entity.setCoverageUniverse(
                domain.getCoverageUniverse() != null
                        ? String.join(",", domain.getCoverageUniverse())
                        : null
        );
        entity.setStarRating(domain.getStarRating());
        entity.setAccuracy12m(domain.getAccuracy12m());
        return entity;
    }
}
