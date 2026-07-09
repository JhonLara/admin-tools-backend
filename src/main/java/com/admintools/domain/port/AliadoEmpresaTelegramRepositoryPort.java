package com.admintools.domain.port;

import com.admintools.domain.model.AliadoEmpresaTelegram;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AliadoEmpresaTelegramRepositoryPort {
    AliadoEmpresaTelegram save(AliadoEmpresaTelegram entity);
    Optional<AliadoEmpresaTelegram> findByAliadoId(UUID aliadoId);
    List<AliadoEmpresaTelegram> findAll();
}
