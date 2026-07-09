package com.admintools.infrastructure.persistence;

import com.admintools.domain.model.AliadoEmpresaTelegram;
import com.admintools.domain.port.AliadoEmpresaTelegramRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public Optional<AliadoEmpresaTelegram> findByAliadoId(UUID aliadoId) {
        return jpaRepository.findByAliadoId(aliadoId);
    }

    @Override
    public List<AliadoEmpresaTelegram> findAll() {
        return jpaRepository.findAll();
    }
}
