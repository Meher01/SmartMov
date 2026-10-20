package com.SmartMov.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(
            @Value("${storage.local-directory:uploads}") String localDirectory)
            throws IOException {

        this.uploadDirectory = Paths.get(localDirectory);
        Files.createDirectories(uploadDirectory);
    }

    public String storeFile(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        String fileExtension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension =
                    originalFileName.substring(
                            originalFileName.lastIndexOf("."));
        }

        String storedFileName =
                UUID.randomUUID() + fileExtension;

        Path targetPath = uploadDirectory.resolve(storedFileName);

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return targetPath.toString();
    }

    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new IOException("File not found");
        }

        return Files.readAllBytes(path);
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}