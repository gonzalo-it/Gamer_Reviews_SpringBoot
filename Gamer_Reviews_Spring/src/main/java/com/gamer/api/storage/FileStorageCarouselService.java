package com.gamer.api.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service("carouselFileStorageService")
public class FileStorageCarouselService {

    private final Path root = Path.of("uploads/carrousel");

    public FileStorageCarouselService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear carpeta /uploads/carrousel", e);
        }
    }

    public String saveImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty())
                throw new IllegalArgumentException("El archivo está vacío");

            List<String> tipos = List.of("image/jpeg", "image/png", "image/webp", "image/jpg");
            if (!tipos.contains(file.getContentType())) {
                throw new IllegalArgumentException("Tipo de archivo no permitido");
            }

            String original = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
            String nombreArchivo = UUID.randomUUID().toString() + "_" + original;
            Path destino = root.resolve(nombreArchivo);

            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // devolvemos solo el nombre del archivo (como en C#)
            return nombreArchivo;
        } catch (IOException ex) {
            throw new RuntimeException("Error guardando imagen del carrusel", ex);
        }
    }

    public boolean deleteImage(String fileName) {
        if (fileName == null || fileName.isBlank()) return false;
        try {
            Path destino = root.resolve(fileName);
            return Files.deleteIfExists(destino);
        } catch (IOException e) {
            return false;
        }
    }
}
