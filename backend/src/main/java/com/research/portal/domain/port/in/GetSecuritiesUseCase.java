package com.research.portal.domain.port.in;

import com.research.portal.domain.model.Security;

import java.util.List;
import java.util.Optional;

public interface GetSecuritiesUseCase {

    List<Security> getAllSecurities();

    Optional<Security> getSecurityById(Long id);

    Optional<Security> getSecurityByTicker(String ticker);
}
