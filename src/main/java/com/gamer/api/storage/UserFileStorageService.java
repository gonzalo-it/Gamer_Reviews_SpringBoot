package com.gamer.api.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service("userFileStorageService")
public class UserFileStorageService {

    private final Path root = Path.of("uploads/users");

    public UserFileStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear carpeta uploads/users", e);
        }
    }

    public String saveImage(MultipartFile file, HttpServletRequest request) {
        try {
            if (file == null || file.isEmpty()) return null;

            // Extensiones válidas
            String contentType = file.getContentType();
            if (!contentType.matches("image/(jpeg|png|webp|jpg)")) {
                throw new IllegalArgumentException("Tipo de archivo no permitido");
            }

            String original = Objects.requireNonNull(file.getOriginalFilename());
            String cleanName = original.replaceAll("\\s+", "_");
            String fileName = UUID.randomUUID() + "_" + cleanName;

            Path target = root.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // URL absoluta igual que juegos
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            return baseUrl + "/uploads/users/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error guardando imagen de usuario", e);
        }
    }

    public boolean deleteImage(String fullUrl) {
        try {
            if (fullUrl == null || fullUrl.isBlank()) return false;

            String fileName = fullUrl.substring(fullUrl.lastIndexOf("/") + 1);

            Path filePath = root.resolve(fileName);
            return Files.deleteIfExists(filePath);

        } catch (Exception e) {
            return false;
        }
    }
}
