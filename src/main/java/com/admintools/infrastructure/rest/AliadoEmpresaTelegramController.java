package com.admintools.infrastructure.rest;

import com.admintools.domain.model.AliadoEmpresaTelegram;
import com.admintools.domain.port.AliadoEmpresaTelegramRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/aliado-empresa-telegram")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AliadoEmpresaTelegramController {

    private final AliadoEmpresaTelegramRepositoryPort repository;

    @GetMapping
    public ResponseEntity<AliadoEmpresaTelegram> obtener(
            @RequestParam UUID aliadoId,
            @RequestParam UUID empresaId) {
        return repository.findByAliadoIdAndEmpresaId(aliadoId, empresaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AliadoEmpresaTelegram> guardar(@RequestBody Map<String, String> body) {
        UUID aliadoId = UUID.fromString(body.get("aliadoId"));
        UUID empresaId = UUID.fromString(body.get("empresaId"));
        String telegramChatId = body.get("telegramChatId");

        AliadoEmpresaTelegram entity = repository.findByAliadoIdAndEmpresaId(aliadoId, empresaId)
                .orElse(AliadoEmpresaTelegram.builder()
                        .aliadoId(aliadoId)
                        .empresaId(empresaId)
                        .build());

        entity.setTelegramChatId(telegramChatId);
        return ResponseEntity.ok(repository.save(entity));
    }
}
