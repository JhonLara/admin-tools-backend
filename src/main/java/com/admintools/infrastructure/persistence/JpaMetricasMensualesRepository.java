package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.MetricasMensuales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMetricasMensualesRepository extends JpaRepository<MetricasMensuales, UUID> {
    Optional<MetricasMensuales> findByPeriodo(String periodo);
}
