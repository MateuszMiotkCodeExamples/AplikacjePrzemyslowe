package com.example.library.service;

import com.example.library.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    public List<Book> importBooksFromCsv(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || !original.endsWith(".csv")) {
            throw new IllegalArgumentException("Plik musi mieć rozszerzenie .csv");
        }

        List<Book> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue;
                }

                String title = parts[0].trim();
                String author = parts[1].trim();

                try {
                    int year = Integer.parseInt(parts[2].trim());
                    books.add(new Book(title, author, year));
                } catch (NumberFormatException ex) {
                    continue;
                }
            }

        } catch (IOException ex) {
            throw new RuntimeException("Błąd podczas czytania pliku CSV", ex);
        }

        return books;
    }
}


