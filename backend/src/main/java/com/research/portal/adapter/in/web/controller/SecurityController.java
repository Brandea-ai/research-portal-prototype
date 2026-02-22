package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.SecurityDto;
import com.research.portal.adapter.in.web.mapper.SecurityApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetSecuritiesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/securities")
public class SecurityController {

    private final GetSecuritiesUseCase getSecurities;
    private final SecurityApiMapper mapper;

    public SecurityController(GetSecuritiesUseCase getSecurities,
                               SecurityApiMapper mapper) {
        this.getSecurities = getSecurities;
        this.mapper = mapper;
    }

    @GetMapping
    public List<SecurityDto> getAllSecurities() {
        return getSecurities.getAllSecurities().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityDto> getSecurityById(@PathVariable Long id) {
        return getSecurities.getSecurityById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Security", id));
    }

    @GetMapping(params = "ticker")
    public ResponseEntity<SecurityDto> getSecurityByTicker(@RequestParam String ticker) {
        return getSecurities.getSecurityByTicker(ticker)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Security", "ticker", ticker));
    }
}
