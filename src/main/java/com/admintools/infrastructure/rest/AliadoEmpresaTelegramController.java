package com.admintools.infrastructure.rest;

import com.admintools.domain.model.AliadoEmpresaTelegram;
import com.admintools.domain.port.AliadoEmpresaTelegramRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/aliado-empresa-telegram")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AliadoEmpresaTelegramController {

    private final AliadoEmpresaTelegramRepositoryPort repository;

    @GetMapping
    public ResponseEntity<List<AliadoEmpresaTelegram>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{aliadoId}")
    public ResponseEntity<AliadoEmpresaTelegram> obtener(@PathVariable UUID aliadoId) {
        return repository.findByAliadoId(aliadoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AliadoEmpresaTelegram> guardar(@RequestBody Map<String, String> body) {
        UUID aliadoId = UUID.fromString(body.get("aliadoId"));
        String telegramChatId = body.get("telegramChatId");

        AliadoEmpresaTelegram entity = repository.findByAliadoId(aliadoId)
                .orElse(AliadoEmpresaTelegram.builder()
                        .aliadoId(aliadoId)
                        .build());

        entity.setTelegramChatId(telegramChatId);
        return ResponseEntity.ok(repository.save(entity));
    }
}
