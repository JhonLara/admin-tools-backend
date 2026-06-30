package com.admintools.domain.port;

import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoEmpresa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepositoryPort {
    Empresa save(Empresa empresa);
    Optional<Empresa> findById(UUID id);
    List<Empresa> findAll();
    List<Empresa> findByEstado(EstadoEmpresa estado);
    void deleteById(UUID id);
}
