package com.research.portal.domain.port.out;

import com.research.portal.domain.model.Security;

import java.util.List;
import java.util.Optional;

public interface SecurityRepository {

    List<Security> findAll();

    Optional<Security> findById(Long id);

    Optional<Security> findByTicker(String ticker);
}
