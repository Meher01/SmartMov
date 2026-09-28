package com.SmartMov.controller;

import com.SmartMov.dto.CreateResourceRequest;
import com.SmartMov.dto.ResourceResponse;
import com.SmartMov.dto.UpdateResourceRequest;
import com.SmartMov.entity.Resource;
import com.SmartMov.service.FileStorageService;
import com.SmartMov.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final FileStorageService fileStorageService;

    public ResourceController(
            ResourceService resourceService,
            FileStorageService fileStorageService) {

        this.resourceService = resourceService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResourceResponse createResource(
            @Valid @RequestBody CreateResourceRequest request,
            Authentication authentication) {

        return resourceService.createResource(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public List<ResourceResponse> getAllResources(
            Authentication authentication) {

        return resourceService.getAllResources(
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public ResourceResponse getResourceById(
            @PathVariable Long id,
            Authentication authentication) {

        return resourceService.getResourceById(
                id,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteResource(
        @PathVariable Long id,
        Authentication authentication) throws IOException {

    resourceService.deleteResource(id, authentication.getName());
}

    @GetMapping("/target/{targetId}")
    public List<ResourceResponse> getResourcesByTargetId(
            @PathVariable Long targetId,
            Authentication authentication) {

        return resourceService.getResourcesByTargetId(
                targetId,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public ResourceResponse updateResource(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResourceRequest request,
            Authentication authentication) {

        return resourceService.updateResource(
                id,
                request,
                authentication.getName()
        );
    }

    @PostMapping("/file")
    public ResourceResponse createFileResource(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetId") Long targetId,
            @RequestParam(value = "content", required = false) String content,
            Authentication authentication)
            throws IOException {

        return resourceService.createFileResource(
                title,
                file,
                targetId,
                content,
                authentication.getName()
        );
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long id,
            Authentication authentication)
            throws IOException {

        Resource resource =
                resourceService.getFileResource(
                        id,
                        authentication.getName()
                );

        byte[] file =
                fileStorageService.getFile(
                        resource.getFilePath()
                );

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (resource.getContentType() != null) {

            mediaType =
                    MediaType.parseMediaType(
                            resource.getContentType()
                    );
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + resource.getFileName()
                                + "\""
                )
                .body(file);
    }

    
}