package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.SesionActiva;
import com.admintools.domain.port.SesionActivaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SesionActivaRepositoryAdapter implements SesionActivaRepositoryPort {

    private final JpaSesionActivaRepository jpaRepository;

    @Override
    public SesionActiva save(SesionActiva sesion) {
        return jpaRepository.save(sesion);
    }

    @Override
    public Optional<SesionActiva> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<SesionActiva> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public List<SesionActiva> findAllByActivaTrue() {
        return jpaRepository.findAllByActivaTrue();
    }

    @Override
    public List<SesionActiva> findAllByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha) {
        return jpaRepository.findAllByActivaTrueAndFechaExpiracionAfter(fecha);
    }

    @Override
    public long countByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha) {
        return jpaRepository.countByActivaTrueAndFechaExpiracionAfter(fecha);
    }

    @Override
    public List<SesionActiva> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByUsername(String username) {
        return jpaRepository.countByUsername(username);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<SesionActiva> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin) {
        return jpaRepository.findByFechaInicioBetween(inicio, fin);
    }

    @Override
    public long countByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin) {
        return jpaRepository.countByFechaInicioBetween(inicio, fin);
    }

    @Override
    public void deleteByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin) {
        jpaRepository.deleteByFechaInicioBetween(inicio, fin);
    }

    @Override
    @Transactional
    public void invalidateByToken(String token) {
        jpaRepository.invalidateByToken(token);
    }

    @Override
    @Transactional
    public void invalidateByUsername(String username) {
        jpaRepository.invalidateByUsername(username);
    }
}
