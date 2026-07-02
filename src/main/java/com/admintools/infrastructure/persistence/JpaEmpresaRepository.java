package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaEmpresaRepository extends JpaRepository<Empresa, UUID> {
    List<Empresa> findByEstado(EstadoEmpresa estado);
    long count();
}
