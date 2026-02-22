package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.AnalystDto;
import com.research.portal.adapter.in.web.mapper.AnalystApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetAnalystsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysts")
public class AnalystController {

    private final GetAnalystsUseCase getAnalysts;
    private final AnalystApiMapper mapper;

    public AnalystController(GetAnalystsUseCase getAnalysts,
                              AnalystApiMapper mapper) {
        this.getAnalysts = getAnalysts;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AnalystDto> getAllAnalysts() {
        return getAnalysts.getAllAnalysts().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalystDto> getAnalystById(@PathVariable Long id) {
        return getAnalysts.getAnalystById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst", id));
    }
}
