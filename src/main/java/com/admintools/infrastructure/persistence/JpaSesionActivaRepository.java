package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.SesionActiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaSesionActivaRepository extends JpaRepository<SesionActiva, UUID> {

    Optional<SesionActiva> findByToken(String token);

    List<SesionActiva> findAllByActivaTrue();

    List<SesionActiva> findAllByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha);

    long countByActivaTrueAndFechaExpiracionAfter(LocalDateTime fecha);

    long countByUsername(String username);

    List<SesionActiva> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    void deleteByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.token = :token")
    void invalidateByToken(@Param("token") String token);

    @Modifying
    @Query("UPDATE SesionActiva s SET s.activa = false WHERE s.username = :username AND s.activa = true")
    void invalidateByUsername(@Param("username") String username);
}
