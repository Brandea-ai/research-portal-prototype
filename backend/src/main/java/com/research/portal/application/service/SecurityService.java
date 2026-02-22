package com.research.portal.application.service;

import com.research.portal.domain.model.Security;
import com.research.portal.domain.port.in.GetSecuritiesUseCase;
import com.research.portal.domain.port.out.SecurityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityService implements GetSecuritiesUseCase {

    private final SecurityRepository securityRepository;

    public SecurityService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @Override
    public List<Security> getAllSecurities() {
        return securityRepository.findAll();
    }

    @Override
    public Optional<Security> getSecurityById(Long id) {
        return securityRepository.findById(id);
    }

    @Override
    public Optional<Security> getSecurityByTicker(String ticker) {
        return securityRepository.findByTicker(ticker);
    }
}
