package com.example.spring_rest_controller_2.service;

import com.example.spring_rest_controller_2.exception.StorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    private final Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileService() {
        // Inicjalizacja ścieżki do przechowywania plików
        this.fileStorageLocation = Paths.get("./uploads")
                .toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            // Tworzenie katalogu jeśli nie istnieje
            Files.createDirectories(fileStorageLocation);
            log.info("Utworzono katalog dla plików: {}", fileStorageLocation);
        } catch (IOException ex) {
            throw new StorageException("Nie można utworzyć katalogu do przechowywania plików.", ex);
        }
    }

    public String saveFile(MultipartFile file) {
        validateFile(file);

        try {
            // Generowanie bezpiecznej nazwy pliku
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // Tworzenie pełnej ścieżki
            Path targetLocation = fileStorageLocation.resolve(newFileName);

            // Kopiowanie pliku
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Zapisano plik: {} jako {}", originalFileName, newFileName);

            return newFileName;
        } catch (IOException ex) {
            throw new StorageException("Nie udało się zapisać pliku.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new StorageException("Plik nie został znaleziony: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new StorageException("Plik nie został znaleziony: " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("Usunięto plik: {}", fileName);
        } catch (IOException ex) {
            throw new StorageException("Nie udało się usunąć pliku: " + fileName, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Nie można zapisać pustego pliku.");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Sprawdzanie bezpieczeństwa nazwy pliku
        if (fileName.contains("..")) {
            throw new StorageException("Nazwa pliku zawiera niedozwoloną ścieżkę: " + fileName);
        }

        // Sprawdzanie rozmiaru pliku (np. max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new StorageException("Rozmiar pliku przekracza dozwolony limit 5MB");
        }

        // Sprawdzanie typu pliku
        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!isImageExtension(fileExtension)) {
            throw new StorageException("Dozwolone są tylko pliki obrazów (jpg, jpeg, png, gif)");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private boolean isImageExtension(String extension) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        return allowedExtensions.contains(extension.toLowerCase());
    }

    // Metoda pomocnicza do generowania URL dla obrazów
    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return "/products/image/" + fileName;
    }
}
