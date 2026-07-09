package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.AliadoEmpresaTelegram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAliadoEmpresaTelegramRepository extends JpaRepository<AliadoEmpresaTelegram, UUID> {
    Optional<AliadoEmpresaTelegram> findByAliadoId(UUID aliadoId);
}
