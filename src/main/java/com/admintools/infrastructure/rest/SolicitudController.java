package com.admintools.infrastructure.rest;

import com.admintools.application.dto.CrearSolicitudRequest;
import com.admintools.application.dto.SolicitudResponse;
import com.admintools.application.usecase.SolicitudUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudUseCase solicitudUseCase;

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listar() {
        return ResponseEntity.ok(solicitudUseCase.listarSolicitudes());
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudResponse>> listarPorAnalista(@RequestParam UUID analistaId) {
        return ResponseEntity.ok(solicitudUseCase.listarSolicitudesPorAnalista(analistaId));
    }

    @GetMapping("/mis-solicitudes-vendedor")
    public ResponseEntity<List<SolicitudResponse>> listarPorVendedor(Authentication authentication) {
        return ResponseEntity.ok(solicitudUseCase.listarSolicitudesPorVendedor(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.obtenerSolicitud(id));
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody CrearSolicitudRequest request, Authentication authentication) {
        return ResponseEntity.ok(solicitudUseCase.crearSolicitud(request, authentication.getName()));
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<SolicitudResponse> validar(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.validarSolicitud(id));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudResponse> rechazar(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.rechazarSolicitud(id));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudResponse> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.aprobarSolicitud(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<SolicitudResponse> finalizar(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.finalizarSolicitud(id));
    }

    @PostMapping("/{id}/notificar-observacion")
    public ResponseEntity<SolicitudResponse> notificarObservacion(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.notificarObservacion(id));
    }

    @PatchMapping("/{id}/firma-recibida")
    public ResponseEntity<SolicitudResponse> marcarFirmaRecibida(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.marcarFirmaRecibida(id));
    }

    @PatchMapping("/{id}/revisado")
    public ResponseEntity<SolicitudResponse> marcarRevisado(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudUseCase.marcarRevisado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        solicitudUseCase.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}
