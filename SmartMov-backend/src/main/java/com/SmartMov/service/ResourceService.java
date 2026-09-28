package com.SmartMov.service;

import com.SmartMov.dto.CreateResourceRequest;
import com.SmartMov.dto.ResourceResponse;
import com.SmartMov.dto.UpdateResourceRequest;
import com.SmartMov.entity.Resource;
import com.SmartMov.entity.ResourceType;
import com.SmartMov.entity.Target;
import com.SmartMov.entity.User;
import com.SmartMov.exception.BusinessException;
import com.SmartMov.repository.ResourceRepository;
import com.SmartMov.repository.TargetRepository;
import com.SmartMov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final TargetRepository targetRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ResourceService(
            ResourceRepository resourceRepository,
            TargetRepository targetRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {

        this.resourceRepository = resourceRepository;
        this.targetRepository = targetRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private User getCurrentUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BusinessException("User not found"));
    }

    public List<ResourceResponse> getResourcesByTargetId(
            Long targetId,
            String username) {

        User user = getCurrentUser(username);

        targetRepository.findByIdAndUser(targetId, user)
                .orElseThrow(() ->
                        new BusinessException("Target not found"));

        return resourceRepository
                .findByTargetIdAndTargetUser(targetId, user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ResourceResponse createResource(
            CreateResourceRequest request,
            String username) {

        User user = getCurrentUser(username);

        Target target = targetRepository
                .findByIdAndUser(request.getTargetId(), user)
                .orElseThrow(() ->
                        new BusinessException("Target not found"));

        Resource resource = new Resource();

        resource.setTitle(request.getTitle());
        resource.setType(request.getType());
        resource.setUrl(request.getUrl());
        resource.setContent(request.getContent());
        resource.setTarget(target);

        Resource savedResource =
                resourceRepository.save(resource);

        return toResponse(savedResource);
    }

    public ResourceResponse createFileResource(
            String title,
            MultipartFile file,
            Long targetId,
            String content,
            String username) throws IOException {

        if (file.isEmpty()) {
            throw new BusinessException("File is required");
        }

        User user = getCurrentUser(username);

        Target target = targetRepository
                .findByIdAndUser(targetId, user)
                .orElseThrow(() ->
                        new BusinessException("Target not found"));

        String filePath =
                fileStorageService.storeFile(file);

        Resource resource = new Resource();

        resource.setTitle(title);
        resource.setType(ResourceType.FILE);
        resource.setFileName(file.getOriginalFilename());
        resource.setFilePath(filePath);
        resource.setContentType(file.getContentType());
        resource.setContent(content);
        resource.setTarget(target);

        Resource savedResource =
                resourceRepository.save(resource);

        return toResponse(savedResource);
    }

    public List<ResourceResponse> getAllResources(
            String username) {

        User user = getCurrentUser(username);

        return resourceRepository
                .findByTargetUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ResourceResponse getResourceById(
        Long resourceId,
        String username) {

    User user = getCurrentUser(username);

    Resource resource =
            resourceRepository.findByIdAndTargetUser(resourceId, user)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Resource not found"));

    return toResponse(resource);
}

    public ResourceResponse updateResource(
            Long resourceId,
            UpdateResourceRequest request,
            String username) {

        User user = getCurrentUser(username);

        Resource resource =
                resourceRepository
                        .findByIdAndTargetUser(resourceId, user)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Resource not found"));

        resource.setTitle(request.getTitle());
        resource.setType(request.getType());
        resource.setUrl(request.getUrl());
        resource.setContent(request.getContent());

        Resource updatedResource =
                resourceRepository.save(resource);

        return toResponse(updatedResource);
    }

    public void deleteResource(
            Long resourceId,
            String username) throws IOException {

        User user = getCurrentUser(username);

        Resource resource =
                resourceRepository
                        .findByIdAndTargetUser(resourceId, user)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Resource not found"));

        if (resource.getType() == ResourceType.FILE
                && resource.getFilePath() != null) {

            fileStorageService.deleteFile(
                    resource.getFilePath());
        }

        resourceRepository.delete(resource);
    }

    public Resource getFileResource(
            Long resourceId,
            String username) {

        User user = getCurrentUser(username);

        Resource resource =
                resourceRepository
                        .findByIdAndTargetUser(resourceId, user)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Resource not found"));

        if (resource.getType() != ResourceType.FILE) {
            throw new BusinessException(
                    "Resource is not a file");
        }

        return resource;
    }

    private ResourceResponse toResponse(Resource resource) {

        ResourceResponse response =
                new ResourceResponse();

        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setType(resource.getType());
        response.setUrl(resource.getUrl());
        response.setContent(resource.getContent());
        response.setTargetId(
                resource.getTarget().getId());
        response.setFileName(resource.getFileName());
        response.setFilePath(resource.getFilePath());
        response.setContentType(resource.getContentType());

        return response;
    }
}