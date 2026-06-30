package com.admintools.infrastructure.rest;

import com.admintools.application.dto.SesionActivaResponse;
import com.admintools.application.dto.SesionResumenResponse;
import com.admintools.application.usecase.SesionActivaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SesionController {

    private final SesionActivaUseCase sesionUseCase;

    @GetMapping
    public ResponseEntity<List<SesionActivaResponse>> listar() {
        return ResponseEntity.ok(sesionUseCase.listarActivas());
    }

    @GetMapping("/conteo")
    public ResponseEntity<Map<String, Long>> conteo() {
        return ResponseEntity.ok(Map.of(
                "activas", sesionUseCase.contarActivas(),
                "total", sesionUseCase.contarTotalSesiones()
        ));
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<SesionResumenResponse>> resumen() {
        return ResponseEntity.ok(sesionUseCase.listarResumenPorUsuario());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> invalidar(@PathVariable UUID id) {
        sesionUseCase.invalidarSesion(id);
        return ResponseEntity.ok().build();
    }
}
