package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.HorarioAnalista;
import com.admintools.domain.port.HorarioAnalistaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HorarioAnalistaRepositoryAdapter implements HorarioAnalistaRepositoryPort {

    private final JpaHorarioAnalistaRepository jpaRepository;

    @Override
    public HorarioAnalista save(HorarioAnalista horario) {
        return jpaRepository.save(horario);
    }

    @Override
    public Optional<HorarioAnalista> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<HorarioAnalista> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<HorarioAnalista> findByAnalistaId(UUID analistaId) {
        return jpaRepository.findByAnalistaId(analistaId);
    }

    @Override
    public List<HorarioAnalista> findByAnalistaIdAndActivoTrue(UUID analistaId) {
        return jpaRepository.findByAnalistaIdAndActivoTrue(analistaId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
