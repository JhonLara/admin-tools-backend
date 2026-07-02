package com.admintools.domain.port;

import com.admintools.domain.model.MetricasMensuales;

import java.util.Optional;

public interface MetricasMensualesRepositoryPort {
    MetricasMensuales save(MetricasMensuales metricas);
    Optional<MetricasMensuales> findByPeriodo(String periodo);
}
