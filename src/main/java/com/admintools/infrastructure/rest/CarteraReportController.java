package com.admintools.infrastructure.rest;

import com.admintools.application.dto.report.CreditsByMaturityReportDto;
import com.admintools.application.dto.report.LoanListReportDto;
import com.admintools.application.dto.report.PaymentsDetailsReportDto;
import com.admintools.domain.service.CarteraReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class CarteraReportController {

    private final CarteraReportService carteraReportService;

    @GetMapping("/credits-by-maturity")
    public ResponseEntity<CreditsByMaturityReportDto> getCreditsByMaturity(
            @RequestParam String cutoffDate,
            @RequestParam(required = false) String creditType,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String clientGroup) {
        CreditsByMaturityReportDto result = carteraReportService.getCreditsByMaturity(cutoffDate, creditType, branch, clientGroup);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/loan-list")
    public ResponseEntity<LoanListReportDto> getLoanList(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String creditType,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String clientGroup) {
        LoanListReportDto result = carteraReportService.getLoanList(startDate, endDate, creditType, branch, clientGroup);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/payments-details")
    public ResponseEntity<PaymentsDetailsReportDto> getPaymentsDetails(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String branch) {
        PaymentsDetailsReportDto result = carteraReportService.getPaymentsDetails(startDate, endDate, branch);
        return ResponseEntity.ok(result);
    }
}
