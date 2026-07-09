package com.admintools.infrastructure.rest;

import com.admintools.application.dto.AliadoRequest;
import com.admintools.application.dto.AliadoResponse;
import com.admintools.application.usecase.AliadoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aliados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AliadoController {

    private final AliadoUseCase aliadoUseCase;

    @GetMapping
    public ResponseEntity<List<AliadoResponse>> listar() {
        return ResponseEntity.ok(aliadoUseCase.listar());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AliadoResponse>> listarActivos() {
        return ResponseEntity.ok(aliadoUseCase.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AliadoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(aliadoUseCase.obtener(id));
    }

    @PostMapping
    public ResponseEntity<AliadoResponse> crear(@Valid @RequestBody AliadoRequest request) {
        return ResponseEntity.ok(aliadoUseCase.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AliadoResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody AliadoRequest request) {
        return ResponseEntity.ok(aliadoUseCase.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<AliadoResponse> cambiarEstado(@PathVariable UUID id) {
        return ResponseEntity.ok(aliadoUseCase.cambiarEstado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        aliadoUseCase.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
