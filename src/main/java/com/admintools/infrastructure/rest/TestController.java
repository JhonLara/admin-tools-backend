package com.admintools.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/error")
    public ResponseEntity<Void> error() {
        throw new RuntimeException("Esto es un error de prueba simulado");
    }

    @GetMapping("/conflicto")
    public ResponseEntity<Void> conflicto() {
        throw new IllegalStateException("Esto es un error de estado simulado");
    }
}
