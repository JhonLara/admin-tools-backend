package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.MetricasMensuales;
import com.admintools.domain.port.MetricasMensualesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MetricasMensualesRepositoryAdapter implements MetricasMensualesRepositoryPort {

    private final JpaMetricasMensualesRepository jpaRepository;

    @Override
    public MetricasMensuales save(MetricasMensuales metricas) {
        return jpaRepository.save(metricas);
    }

    @Override
    public Optional<MetricasMensuales> findByPeriodo(String periodo) {
        return jpaRepository.findByPeriodo(periodo);
    }
}
