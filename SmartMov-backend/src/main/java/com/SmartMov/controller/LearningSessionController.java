package com.SmartMov.controller;

import com.SmartMov.dto.FinishSessionRequest;
import com.SmartMov.dto.LearningSessionResponse;
import com.SmartMov.dto.StartSessionRequest;
import com.SmartMov.service.LearningSessionService;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class LearningSessionController {

    private final LearningSessionService learningSessionService;

    public LearningSessionController(
            LearningSessionService learningSessionService) {
        this.learningSessionService = learningSessionService;
    }

    @PostMapping("/start")
    public LearningSessionResponse startSession(
            @Valid @RequestBody StartSessionRequest request,
            Authentication authentication) {

        return learningSessionService.startSession(
                request.getTargetId(),
                authentication.getName()
        );
    }

    @PostMapping("/finish")
public LearningSessionResponse finishSession(
        @Valid @RequestBody FinishSessionRequest request,
        Authentication authentication) {

    return learningSessionService.finishSession(
            request.getSessionId(),
            authentication.getName()
    );
}

    @GetMapping("/{id}")
    public LearningSessionResponse getSessionById(
            @PathVariable Long id,
            Authentication authentication) {

        return learningSessionService.getSessionById(
                id,
                authentication.getName()
        );
    }

    @GetMapping
public List<LearningSessionResponse> getAllSessions(
        Authentication authentication) {

    return learningSessionService.getAllSessions(
            authentication.getName()
    );
}
}