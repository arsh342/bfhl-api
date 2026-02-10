package com.bfhl.api.controller;

import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;
import com.bfhl.api.dto.HealthResponse;
import com.bfhl.api.service.BfhlService;
import com.bfhl.api.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing POST /bfhl and GET /health endpoints.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BfhlController {

    private final BfhlService bfhlService;
    private final GeminiService geminiService;

    @Value("${bfhl.official.email}")
    private String officialEmail;

    /**
     * POST /bfhl — Processes exactly one of: fibonacci, prime, lcm, hcf, AI.
     */
    @PostMapping("/bfhl")
    public ResponseEntity<BfhlResponse> processBfhl(@RequestBody BfhlRequest request) {

        int presentKeys = countPresentKeys(request);

        if (presentKeys == 0) {
            log.warn("POST /bfhl — no functional key provided");
            return ResponseEntity.badRequest().body(
                    BfhlResponse.builder()
                            .success(false)
                            .officialEmail(officialEmail)
                            .data("Request must contain exactly one key: fibonacci, prime, lcm, hcf, or AI")
                            .build()
            );
        }

        if (presentKeys > 1) {
            log.warn("POST /bfhl — multiple functional keys provided");
            return ResponseEntity.badRequest().body(
                    BfhlResponse.builder()
                            .success(false)
                            .officialEmail(officialEmail)
                            .data("Request must contain exactly one key, but " + presentKeys + " were provided")
                            .build()
            );
        }

        Object result = dispatch(request);

        return ResponseEntity.ok(
                BfhlResponse.builder()
                        .success(true)
                        .officialEmail(officialEmail)
                        .data(result)
                        .build()
        );
    }

    /**
     * GET /health — Simple health check.
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
                HealthResponse.builder()
                        .success(true)
                        .officialEmail(officialEmail)
                        .build()
        );
    }

    // ─── Private helpers ───

    private Object dispatch(BfhlRequest request) {
        if (request.getFibonacci() != null) {
            log.info("Processing fibonacci({})", request.getFibonacci());
            return bfhlService.fibonacci(request.getFibonacci());
        }
        if (request.getPrime() != null) {
            log.info("Processing prime filter on {} values", request.getPrime().size());
            return bfhlService.filterPrimes(request.getPrime());
        }
        if (request.getLcm() != null) {
            log.info("Processing LCM on {} values", request.getLcm().size());
            return bfhlService.computeLcm(request.getLcm());
        }
        if (request.getHcf() != null) {
            log.info("Processing HCF on {} values", request.getHcf().size());
            return bfhlService.computeHcf(request.getHcf());
        }
        if (request.getAi() != null) {
            log.info("Processing AI question");
            return geminiService.ask(request.getAi());
        }
        // Should never reach here due to earlier validation
        throw new IllegalStateException("No functional key matched");
    }

    private int countPresentKeys(BfhlRequest request) {
        int count = 0;
        if (request.getFibonacci() != null) count++;
        if (request.getPrime() != null) count++;
        if (request.getLcm() != null) count++;
        if (request.getHcf() != null) count++;
        if (request.getAi() != null) count++;
        return count;
    }
}
