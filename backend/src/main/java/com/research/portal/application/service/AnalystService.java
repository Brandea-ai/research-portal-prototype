package com.research.portal.application.service;

import com.research.portal.domain.model.Analyst;
import com.research.portal.domain.port.in.GetAnalystsUseCase;
import com.research.portal.domain.port.out.AnalystRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalystService implements GetAnalystsUseCase {

    private final AnalystRepository analystRepository;

    public AnalystService(AnalystRepository analystRepository) {
        this.analystRepository = analystRepository;
    }

    @Override
    public List<Analyst> getAllAnalysts() {
        return analystRepository.findAll();
    }

    @Override
    public Optional<Analyst> getAnalystById(Long id) {
        return analystRepository.findById(id);
    }
}
