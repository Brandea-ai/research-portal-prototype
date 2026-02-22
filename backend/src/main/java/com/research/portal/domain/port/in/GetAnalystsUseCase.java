package com.research.portal.domain.port.in;

import com.research.portal.domain.model.Analyst;

import java.util.List;
import java.util.Optional;

public interface GetAnalystsUseCase {

    List<Analyst> getAllAnalysts();

    Optional<Analyst> getAnalystById(Long id);
}
