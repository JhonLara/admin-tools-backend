package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Aliado;
import com.admintools.domain.model.EstadoAliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaAliadoRepository extends JpaRepository<Aliado, UUID> {
    List<Aliado> findByEstado(EstadoAliado estado);
}
