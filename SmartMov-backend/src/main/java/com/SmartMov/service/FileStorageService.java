package com.SmartMov.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;
    private final String storageProvider;
    private final String bucket;
    private final S3Client s3Client;

    public FileStorageService(
            @Value("${storage.provider:local}") String storageProvider,
            @Value("${storage.local-directory:uploads}") String localDirectory,
            @Value("${aws.region:us-east-1}") String awsRegion,
            @Value("${aws.s3.bucket:}") String bucket)
            throws IOException {

        this.storageProvider = storageProvider.toLowerCase();
        this.bucket = bucket;
        this.uploadDirectory = Paths.get(localDirectory);

        if ("s3".equals(this.storageProvider)) {
            if (bucket.isBlank()) {
                throw new IllegalStateException(
                        "AWS_S3_BUCKET is required when STORAGE_PROVIDER=s3");
            }
            this.s3Client = S3Client.builder()
                    .region(Region.of(awsRegion))
                    .build();
        } else {
            Files.createDirectories(uploadDirectory);
            this.s3Client = null;
        }
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

        if ("s3".equals(storageProvider)) {
            String key = storedFileName;
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize()));

            return "s3://" + bucket + "/" + key;
        }

        Path targetPath = uploadDirectory.resolve(storedFileName);

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return targetPath.toString();
    }

    public byte[] getFile(String filePath) throws IOException {

        if (filePath.startsWith("s3://")) {
            String key = filePath.substring(("s3://" + bucket + "/").length());
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            return s3Client.getObjectAsBytes(request).asByteArray();
        }

        Path path = Paths.get(filePath);

    if (!Files.exists(path)) {
        throw new IOException("File not found");
    }

    return Files.readAllBytes(path);
}

public void deleteFile(String filePath) throws IOException {

    if (filePath.startsWith("s3://")) {
        String key = filePath.substring(("s3://" + bucket + "/").length());
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        return;
    }

    Path path = Paths.get(filePath);

    if (Files.exists(path)) {
        Files.delete(path);
    }
}
}