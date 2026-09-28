package com.SmartMov.service;

import com.SmartMov.dto.LearningSessionResponse;
import com.SmartMov.entity.LearningSession;
import com.SmartMov.entity.Target;
import com.SmartMov.exception.BusinessException;
import com.SmartMov.repository.LearningSessionRepository;
import com.SmartMov.repository.TargetRepository;
import org.springframework.stereotype.Service;
import com.SmartMov.entity.User;
import com.SmartMov.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearningSessionService {

    private final LearningSessionRepository learningSessionRepository;
    private final TargetRepository targetRepository;
    private final TargetService targetService;
    private final UserRepository userRepository;

    public LearningSessionService(
        LearningSessionRepository learningSessionRepository,
        TargetRepository targetRepository,
        TargetService targetService,
        UserRepository userRepository) {

    this.learningSessionRepository = learningSessionRepository;
    this.targetRepository = targetRepository;
    this.targetService = targetService;
    this.userRepository = userRepository;
}

    public List<LearningSessionResponse> getAllSessions(String username) {

    User user = getCurrentUser(username);

    return learningSessionRepository
            .findByTargetUser(user)
            .stream()
            .map(this::toResponse)
            .toList();
}

    public LearningSessionResponse startSession(
        Long targetId,
        String username) {

        User user = getCurrentUser(username);

        Target target = targetRepository
        .findByIdAndUser(targetId, user)
        .orElseThrow(() ->
                new BusinessException("Target not found"));

        if (learningSessionRepository
                .existsByTargetIdAndEndedAtIsNull(targetId)) {

            throw new BusinessException(
                    "Target already has an active session"
            );
        }

        LearningSession session = new LearningSession();

        session.setTarget(target);
        session.setStartedAt(LocalDateTime.now());

        LearningSession savedSession =
                learningSessionRepository.save(session);

        return toResponse(savedSession);
    }

    @Transactional
    public LearningSessionResponse finishSession(
        Long sessionId,
        String username) {

    User user = getCurrentUser(username);

    LearningSession session =
        learningSessionRepository
                .findByIdAndTargetUser(sessionId, user)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Session not found"
                        ));

    if (session.getEndedAt() != null) {
        throw new BusinessException(
                "Session is already finished"
        );
    }

    // Finish the session
    LocalDateTime endedAt = LocalDateTime.now();
    session.setEndedAt(endedAt);

    // Calculate session duration
    long durationSeconds = Duration.between(
            session.getStartedAt(),
            endedAt
    ).getSeconds();

    int durationMinutes = (int) Math.ceil(
            durationSeconds / 60.0
    );

    session.setDurationMinutes(durationMinutes);

    // Apply learning progress through TargetService
    Target target = session.getTarget();

    targetService.applyProgress(
            target,
            durationMinutes
    );

    // Save completed session
    LearningSession savedSession =
            learningSessionRepository.save(session);

    return toResponse(savedSession);
}

    public LearningSessionResponse getSessionById(
        Long sessionId,
        String username) {

    User user = getCurrentUser(username);

    LearningSession session =
        learningSessionRepository
                .findByIdAndTargetUser(sessionId, user)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Session not found"
                        ));

    return toResponse(session);
}

    private LearningSessionResponse toResponse(
            LearningSession session) {

        LearningSessionResponse response =
                new LearningSessionResponse();

        response.setId(session.getId());
        response.setSessionId(session.getId());
        response.setTargetId(session.getTarget().getId());
        response.setDurationMinutes(
                session.getDurationMinutes()
        );
        response.setStartedAt(session.getStartedAt());
        response.setEndedAt(session.getEndedAt());

        return response;
    }

    private User getCurrentUser(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "User not found"
                    ));
}
}