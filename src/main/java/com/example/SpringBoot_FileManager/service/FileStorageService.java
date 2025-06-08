package com.example.SpringBoot_FileManager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private static final Path STORAGE_DIRECTORY = Paths.get("uploads").toAbsolutePath().normalize();

    public Path getStorageDirectory() {
        return STORAGE_DIRECTORY;
    }

    public FileStorageService() throws IOException {
        // Create the directory if it doesn't exist
        if (!Files.exists(STORAGE_DIRECTORY)) {
            Files.createDirectories(STORAGE_DIRECTORY);
        }
    }

    public void saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null || fileToSave.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }

        Path targetPath = STORAGE_DIRECTORY.resolve(Paths.get(Objects.requireNonNull(fileToSave.getOriginalFilename()))).normalize();

        // Prevent path traversal attack
        if (!targetPath.startsWith(STORAGE_DIRECTORY)) {
            throw new SecurityException("Invalid file path");
        }

        Files.copy(fileToSave.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String filename) throws Exception{
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename is null or empty");
        }

        // Resolve and normalize the file path securely
        Path fileToDownload = STORAGE_DIRECTORY.resolve(Paths.get(filename)).normalize();

        // Prevent path traversal
        if (!fileToDownload.startsWith(STORAGE_DIRECTORY)) {
            throw new SecurityException("Invalid file path or filename");
        }

        // Check if file exists
        if (!Files.exists(fileToDownload)) {
            throw new FileNotFoundException("No file named: " + filename);
        }

        return fileToDownload.toFile();
    }
}
