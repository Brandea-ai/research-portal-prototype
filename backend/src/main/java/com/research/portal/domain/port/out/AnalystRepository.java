package com.research.portal.domain.port.out;

import com.research.portal.domain.model.Analyst;

import java.util.List;
import java.util.Optional;

public interface AnalystRepository {

    List<Analyst> findAll();

    Optional<Analyst> findById(Long id);
}
