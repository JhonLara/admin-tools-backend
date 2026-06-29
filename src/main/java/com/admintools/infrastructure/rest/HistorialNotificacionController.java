package com.admintools.infrastructure.rest;

import com.admintools.application.dto.HistorialNotificacionResponse;
import com.admintools.application.usecase.HistorialNotificacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historial-notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HistorialNotificacionController {

    private final HistorialNotificacionUseCase historialUseCase;

    @GetMapping
    public ResponseEntity<List<HistorialNotificacionResponse>> listar() {
        return ResponseEntity.ok(historialUseCase.listar());
    }
}
