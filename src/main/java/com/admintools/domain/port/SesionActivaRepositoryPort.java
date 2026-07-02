package com.admintools.domain.port;

import com.admintools.domain.model.SesionActiva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SesionActivaRepositoryPort {
    SesionActiva save(SesionActiva sesion);
    Optional<SesionActiva> findById(UUID id);
    Optional<SesionActiva> findByToken(String token);
    List<SesionActiva> findAllByActivaTrue();
    List<SesionActiva> findAllByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha);
    long countByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha);
    List<SesionActiva> findAll();
    long count();
    long countByUsername(String username);
    List<SesionActiva> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    void deleteByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    void deleteById(UUID id);
    void invalidateByToken(String token);
}
