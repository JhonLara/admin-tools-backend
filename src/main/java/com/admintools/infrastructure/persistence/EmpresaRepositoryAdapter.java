package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Empresa;
import com.admintools.domain.model.EstadoEmpresa;
import com.admintools.domain.port.EmpresaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final JpaEmpresaRepository jpaRepository;

    @Override
    public Empresa save(Empresa empresa) {
        return jpaRepository.save(empresa);
    }

    @Override
    public Optional<Empresa> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Empresa> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Empresa> findByEstado(EstadoEmpresa estado) {
        return jpaRepository.findByEstado(estado);
    }
}
