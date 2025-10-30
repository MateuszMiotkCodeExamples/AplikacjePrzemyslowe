package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.dto.CreateBookRequest;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
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

