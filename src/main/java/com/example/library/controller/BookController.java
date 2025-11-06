package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.dto.CreateBookRequest;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.CsvImportService;
import com.example.library.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;
    private final FileStorageService fileStorageService;
    private final CsvImportService csvImportService;

    public BookController(BookService bookService,
                          FileStorageService fileStorageService,
                          CsvImportService csvImportService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
        this.csvImportService = csvImportService;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = new ArrayList<>();
        
        // Konwersja: Book -> BookDTO
        for (Book book : books) {
            BookDTO dto = new BookDTO(
                book.getTitle(), 
                book.getAuthor(), 
                book.getYear()
            );
            bookDTOs.add(dto);
        }
        
        return ResponseEntity.ok(bookDTOs);
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody CreateBookRequest request) {
        // Walidacja
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Konwersja: CreateBookRequest -> Book
        Book book = new Book(
            request.getTitle(),
            request.getAuthor(),
            request.getYear()
        );
        bookService.addBook(book);
        
        // Konwersja: Book -> BookDTO (odpowiedź)
        BookDTO response = new BookDTO(
            book.getTitle(),
            book.getAuthor(),
            book.getYear()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDTO> createBookWithCover(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("year") int year,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Book book = new Book(title, author, year);

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                String filename = fileStorageService.storeFile(coverFile, title);
                book.setCoverImageFilename(filename);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        bookService.addBook(book);

        BookDTO response = new BookDTO(
            book.getTitle(),
            book.getAuthor(),
            book.getYear()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{title}/cover")
    public ResponseEntity<Resource> downloadCover(@PathVariable String title) {
        List<Book> books = bookService.getAllBooks();
        Book book = books.stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        if (book == null || book.getCoverImageFilename() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.loadFile(book.getCoverImageFilename());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importBooks(@RequestParam("file") MultipartFile file) {
        try {
            List<Book> books = csvImportService.importBooksFromCsv(file);
            for (Book b : books) {
                bookService.addBook(b);
            }
            return ResponseEntity.ok("Zaimportowano " + books.size() + " książek");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/author/{authorName}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable String authorName) {
        List<Book> books = bookService.findBooksByAuthor(authorName);
        
        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            bookDTOs.add(new BookDTO(
                book.getTitle(),
                book.getAuthor(),
                book.getYear()
            ));
        }
        
        return ResponseEntity.ok(bookDTOs);
    }
}

