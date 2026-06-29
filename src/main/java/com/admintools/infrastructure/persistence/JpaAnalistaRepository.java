package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Analista;
import com.admintools.domain.model.EstadoAnalista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAnalistaRepository extends JpaRepository<Analista, UUID> {
    List<Analista> findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista estado);
    Optional<Analista> findByCedula(String cedula);
}
