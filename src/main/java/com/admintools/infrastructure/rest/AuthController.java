package com.admintools.infrastructure.rest;

import com.admintools.application.dto.LoginRequest;
import com.admintools.application.dto.LoginResponse;
import com.admintools.application.dto.MeResponse;
import com.admintools.application.usecase.AuthUseCase;
import com.admintools.domain.model.Usuario;
import com.admintools.domain.port.UsuarioRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return ResponseEntity.ok(authUseCase.login(request, ip, userAgent));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Optional<Usuario> usuario = usuarioRepository.findByUsername(authentication.getName());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(MeResponse.builder()
                .username(usuario.get().getUsername())
                .nombre(usuario.get().getNombre())
                .rol(usuario.get().getRol())
                .analistaId(usuario.get().getAnalistaId())
                .build());
    }
}
