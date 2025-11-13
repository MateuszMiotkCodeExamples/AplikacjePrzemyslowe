package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.CsvImportService;
import com.example.library.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerFileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private CsvImportService csvImportService;

    @Test
    void shouldUploadBookWithCover() throws Exception {
        MockMultipartFile coverFile = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        when(fileStorageService.storeFile(any(), anyString()))
                .thenReturn("book_title_12345678.jpg");

        doNothing().when(bookService).addBook(any());

        mockMvc.perform(multipart("/api/books")
                        .file(coverFile)
                        .param("title", "Test Book")
                        .param("author", "Test Author")
                        .param("year", "2025"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(fileStorageService).storeFile(any(), eq("Test Book"));
        verify(bookService).addBook(any());
    }

    @Test
    void shouldCreateBookWithoutCover() throws Exception {
        doNothing().when(bookService).addBook(any());

        mockMvc.perform(multipart("/api/books")
                        .param("title", "Book Without Cover")
                        .param("author", "Author")
                        .param("year", "2025"))
                .andExpect(status().isCreated());

        verify(fileStorageService, never()).storeFile(any(), any());
        verify(bookService).addBook(any());
    }

    @Test
    void shouldRejectInvalidFileExtension() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "cover",
                "document.exe",
                "application/exe",
                "malicious content".getBytes()
        );

        when(fileStorageService.storeFile(any(), anyString()))
                .thenThrow(new IllegalArgumentException("Niedozwolone rozszerzenie"));

        mockMvc.perform(multipart("/api/books")
                        .file(invalidFile)
                        .param("title", "Test")
                        .param("author", "Author")
                        .param("year", "2025"))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).addBook(any());
    }

    @Test
    void shouldDownloadCover() throws Exception {
        Book book = new Book("Test Book", "Author", 2025);
        book.setCoverImageFilename("cover_12345.jpg");

        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookService.getAllBooks()).thenReturn(books);

        Path mockPath = Paths.get("uploads/cover_12345.jpg");
        Files.createDirectories(mockPath.getParent());
        Files.writeString(mockPath, "dummy");
        when(fileStorageService.loadFile("cover_12345.jpg")).thenReturn(mockPath);

        mockMvc.perform(get("/api/books/{title}/cover", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION));

        verify(fileStorageService).loadFile("cover_12345.jpg");
    }

    @Test
    void shouldReturn404WhenCoverNotFound() throws Exception {
        Book book = new Book("Book Without Cover", "Author", 2025);

        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books/{title}/cover", "Book Without Cover"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldImportBooksFromCsv() throws Exception {
        String csvContent = """
                title,author,year
                Book One,Author One,2020
                Book Two,Author Two,2021
                """;

        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "books.csv",
                "text/csv",
                csvContent.getBytes()
        );

        List<Book> importedBooks = new ArrayList<>();
        importedBooks.add(new Book("Book One", "Author One", 2020));
        importedBooks.add(new Book("Book Two", "Author Two", 2021));

        when(csvImportService.importBooksFromCsv(any())).thenReturn(importedBooks);
        doNothing().when(bookService).addBook(any());

        mockMvc.perform(multipart("/api/books/import")
                        .file(csvFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Zaimportowano 2 książek"));

        verify(bookService, times(2)).addBook(any());
    }
}


