package com.admintools.infrastructure.rest;

import com.admintools.application.dto.AnalistaRequest;
import com.admintools.application.dto.AnalistaResponse;
import com.admintools.application.usecase.AnalistaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analistas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalistaController {

    private final AnalistaUseCase analistaUseCase;

    @GetMapping
    public ResponseEntity<List<AnalistaResponse>> listar() {
        return ResponseEntity.ok(analistaUseCase.listar());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AnalistaResponse>> listarActivos() {
        return ResponseEntity.ok(analistaUseCase.listarActivos());
    }

    @PostMapping
    public ResponseEntity<AnalistaResponse> crear(@Valid @RequestBody AnalistaRequest request) {
        return ResponseEntity.ok(analistaUseCase.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalistaResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody AnalistaRequest request) {
        return ResponseEntity.ok(analistaUseCase.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<AnalistaResponse> cambiarEstado(@PathVariable UUID id) {
        return ResponseEntity.ok(analistaUseCase.cambiarEstado(id));
    }
}
