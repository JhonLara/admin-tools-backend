package com.admintools.infrastructure.rest;

import com.admintools.application.dto.EmpresaRequest;
import com.admintools.application.dto.EmpresaResponse;
import com.admintools.application.usecase.EmpresaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaUseCase empresaUseCase;

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> listar() {
        return ResponseEntity.ok(empresaUseCase.listar());
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> crear(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaUseCase.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaUseCase.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EmpresaResponse> cambiarEstado(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaUseCase.cambiarEstado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        empresaUseCase.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
