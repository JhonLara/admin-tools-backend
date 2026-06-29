package com.admintools.infrastructure.rest;

import com.admintools.application.dto.DashboardResumen;
import com.admintools.application.usecase.DashboardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumen> resumen() {
        return ResponseEntity.ok(dashboardUseCase.resumen());
    }
}
