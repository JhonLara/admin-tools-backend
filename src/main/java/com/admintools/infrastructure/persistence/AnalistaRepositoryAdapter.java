package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.Analista;
import com.admintools.domain.model.EstadoAnalista;
import com.admintools.domain.port.AnalistaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalistaRepositoryAdapter implements AnalistaRepositoryPort {

    private final JpaAnalistaRepository jpaRepository;

    @Override
    public Analista save(Analista analista) {
        return jpaRepository.save(analista);
    }

    @Override
    public Optional<Analista> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Analista> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Analista> findByEstadoOrderByOrdenAsignacionAsc(EstadoAnalista estado) {
        return jpaRepository.findByEstadoOrderByOrdenAsignacionAsc(estado);
    }

    @Override
    public Optional<Analista> findByCedula(String cedula) {
        return jpaRepository.findByCedula(cedula);
    }
}
