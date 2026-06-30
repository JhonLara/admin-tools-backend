package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.AliadoEmpresaTelegram;
import com.admintools.domain.port.AliadoEmpresaTelegramRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AliadoEmpresaTelegramAdapter implements AliadoEmpresaTelegramRepositoryPort {

    private final JpaAliadoEmpresaTelegramRepository jpaRepository;

    @Override
    public AliadoEmpresaTelegram save(AliadoEmpresaTelegram entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<AliadoEmpresaTelegram> findByAliadoIdAndEmpresaId(UUID aliadoId, UUID empresaId) {
        return jpaRepository.findByAliadoIdAndEmpresaId(aliadoId, empresaId);
    }
}
