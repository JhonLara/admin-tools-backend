package com.admintools.infrastructure.rest;

import com.admintools.application.dto.HorarioAnalistaRequest;
import com.admintools.application.dto.HorarioAnalistaResponse;
import com.admintools.application.usecase.HorarioAnalistaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/horarios-analistas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioAnalistaController {

    private final HorarioAnalistaUseCase horarioUseCase;

    @PostMapping
    public ResponseEntity<List<HorarioAnalistaResponse>> crear(@Valid @RequestBody HorarioAnalistaRequest request) {
        return ResponseEntity.ok(horarioUseCase.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioAnalistaResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody HorarioAnalistaRequest request) {
        return ResponseEntity.ok(horarioUseCase.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        horarioUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HorarioAnalistaResponse>> listar() {
        return ResponseEntity.ok(horarioUseCase.listar());
    }

    @GetMapping("/analista/{analistaId}")
    public ResponseEntity<List<HorarioAnalistaResponse>> listarPorAnalista(@PathVariable UUID analistaId) {
        return ResponseEntity.ok(horarioUseCase.listarPorAnalista(analistaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioAnalistaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(horarioUseCase.obtener(id));
    }
}
