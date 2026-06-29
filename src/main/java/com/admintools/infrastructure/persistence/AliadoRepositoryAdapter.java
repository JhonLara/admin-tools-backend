package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Aliado;
import com.admintools.domain.model.EstadoAliado;
import com.admintools.domain.port.AliadoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AliadoRepositoryAdapter implements AliadoRepositoryPort {

    private final JpaAliadoRepository jpaRepository;

    @Override
    public Aliado save(Aliado aliado) {
        return jpaRepository.save(aliado);
    }

    @Override
    public Optional<Aliado> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Aliado> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Aliado> findByEstado(EstadoAliado estado) {
        return jpaRepository.findByEstado(estado);
    }

    @Override
    public List<Aliado> findAllByIdIn(List<UUID> ids) {
        return jpaRepository.findAllById(ids);
    }
}
