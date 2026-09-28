package com.SmartMov.controller;


import com.SmartMov.dto.CreateTargetRequest;
import com.SmartMov.dto.ProgressRequest;
import com.SmartMov.dto.TargetResponse;
import com.SmartMov.dto.UpdateTargetRequest;
import com.SmartMov.service.TargetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/targets")
public class TargetController {

    private final TargetService targetService;

    public TargetController(TargetService targetService) {
        this.targetService = targetService;
    }

    @GetMapping
    public List<TargetResponse> getAllTargets(
            Authentication authentication) {

        return targetService.getAllTargets(
                authentication.getName()
        );
    }

    @PostMapping
    public TargetResponse createTarget(
            @Valid @RequestBody CreateTargetRequest request,
            Authentication authentication) {

        return targetService.createTarget(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public TargetResponse getTargetById(
            @PathVariable Long id,
            Authentication authentication) {

        return targetService.getTargetById(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public TargetResponse updateTarget(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTargetRequest request,
            Authentication authentication) {

        return targetService.updateTarget(
                id,
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarget(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        targetService.deleteTarget(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/progress")
public TargetResponse addProgress(
        @PathVariable Long id,
        @Valid @RequestBody ProgressRequest request,
        Authentication authentication) {

    return targetService.addProgress(
            id,
            request,
            authentication.getName()
    );
}
}