package com.research.portal.adapter.in.web.mapper;

import com.research.portal.adapter.in.web.dto.AuditLogDto;
import com.research.portal.domain.model.AuditLog;
import org.springframework.stereotype.Component;

/**
 * Mapper zwischen AuditLog Domain-Modell und AuditLogDto (REST API).
 *
 * Konvertiert Domain-Objekte in DTOs für die API-Antworten.
 * Die Domain-Schicht bleibt so unabhängig von der Web-Schicht.
 */
@Component
public class AuditLogApiMapper {

    /**
     * Konvertiert ein AuditLog Domain-Modell in ein DTO.
     *
     * @param domain das Domain-Modell
     * @return das DTO für die API-Antwort
     */
    public AuditLogDto toDto(AuditLog domain) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(domain.getId());
        dto.setTimestamp(domain.getTimestamp());
        dto.setAction(domain.getAction().name());
        dto.setEntityType(domain.getEntityType());
        dto.setEntityId(domain.getEntityId());
        dto.setUserId(domain.getUserId());
        dto.setUserName(domain.getUserName());
        dto.setUserRole(domain.getUserRole());
        dto.setDetails(domain.getDetails());
        dto.setIpAddress(domain.getIpAddress());
        return dto;
    }
}
