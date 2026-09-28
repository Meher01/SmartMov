package com.SmartMov.service;

import java.time.LocalDate;
import com.SmartMov.dto.ProgressRequest;
import com.SmartMov.dto.CreateTargetRequest;
import com.SmartMov.dto.TargetResponse;
import com.SmartMov.dto.UpdateTargetRequest;
import com.SmartMov.entity.Resource;
import com.SmartMov.entity.Target;
import com.SmartMov.entity.User;
import com.SmartMov.repository.LearningSessionRepository;
import com.SmartMov.repository.ResourceRepository;
import com.SmartMov.repository.TargetRepository;
import com.SmartMov.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class TargetService {

    private final TargetRepository targetRepository;
        private final ResourceRepository resourceRepository;
        private final LearningSessionRepository learningSessionRepository;
    private final UserRepository userRepository;
        private final FileStorageService fileStorageService;

    public TargetService(
            TargetRepository targetRepository,
                        ResourceRepository resourceRepository,
                        LearningSessionRepository learningSessionRepository,
                        FileStorageService fileStorageService,
            UserRepository userRepository) {

        this.targetRepository = targetRepository;
                this.resourceRepository = resourceRepository;
                this.learningSessionRepository = learningSessionRepository;
                this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        ));
    }

    public List<TargetResponse> getAllTargets(String username) {

        User user = getCurrentUser(username);

        return targetRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TargetResponse createTarget(
            CreateTargetRequest request,
            String username) {

        User user = getCurrentUser(username);

        Target target = new Target();

        target.setTitle(request.getTitle());
        target.setDescription(request.getDescription());
        target.setCategory(request.getCategory());
        target.setDailyMinutes(request.getDailyMinutes());

target.setCompletedMinutes(0);
target.setCompletedToday(false);
target.setProgressDate(LocalDate.now());
target.setUser(user);

        Target savedTarget = targetRepository.save(target);

        return toResponse(savedTarget);
    }

    public TargetResponse getTargetById(
            Long id,
            String username) {

        User user = getCurrentUser(username);

        Target target = targetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Target not found"
                        ));

        return toResponse(target);
    }

    public TargetResponse updateTarget(
            Long id,
            UpdateTargetRequest request,
            String username) {

        User user = getCurrentUser(username);

        Target target = targetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Target not found"
                        ));

        target.setTitle(request.getTitle());
        target.setDescription(request.getDescription());
        target.setCategory(request.getCategory());
        target.setDailyMinutes(request.getDailyMinutes());

        Target updatedTarget = targetRepository.save(target);

        return toResponse(updatedTarget);
    }

    @Transactional
    public void deleteTarget(
            Long id,
            String username) throws IOException {

        User user = getCurrentUser(username);

        Target target = targetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Target not found"
                        ));

        List<Resource> resources =
                resourceRepository.findByTargetIdAndTargetUser(id, user);

        for (Resource resource : resources) {
            if (resource.getFilePath() != null) {
                fileStorageService.deleteFile(resource.getFilePath());
            }
        }

        learningSessionRepository.deleteAll(
                learningSessionRepository.findByTargetIdAndTargetUser(id, user)
        );
        resourceRepository.deleteAll(resources);
        targetRepository.delete(target);
    }

    public TargetResponse addProgress(
        Long id,
        ProgressRequest request,
        String username) {

    User user = getCurrentUser(username);

    Target target = targetRepository
            .findByIdAndUser(id, user)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Target not found"
                    ));

    return applyProgress(
            target,
            request.getMinutes()
    );
}

public TargetResponse applyProgress(
        Target target,
        int minutes) {

    LocalDate today = LocalDate.now();

    // Reset progress when a new day starts
    if (target.getProgressDate() == null
            || !target.getProgressDate().equals(today)) {

        target.setCompletedMinutes(0);
        target.setCompletedToday(false);
        target.setProgressDate(today);
    }

    int currentMinutes = target.getCompletedMinutes() == null
            ? 0
            : target.getCompletedMinutes();

    int dailyMinutes = target.getDailyMinutes();

    int updatedMinutes = Math.min(
            currentMinutes + minutes,
            dailyMinutes
    );

    target.setCompletedMinutes(updatedMinutes);
    target.setCompletedToday(updatedMinutes >= dailyMinutes);

    Target updatedTarget = targetRepository.save(target);

    return toResponse(updatedTarget);
}

    private TargetResponse toResponse(Target target) {

        TargetResponse response = new TargetResponse();

        response.setId(target.getId());
        response.setTitle(target.getTitle());
        response.setDescription(target.getDescription());
        response.setCategory(target.getCategory());
        response.setDailyMinutes(target.getDailyMinutes());
        response.setCompletedMinutes(target.getCompletedMinutes());
        response.setCompletedToday(target.getCompletedToday());
        response.setProgressDate(target.getProgressDate());

        return response;
    }
}