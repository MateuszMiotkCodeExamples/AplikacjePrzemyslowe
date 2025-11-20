package com.example.library.service;

import com.example.library.dao.BookDAO;
import com.example.library.model.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Transactional
    public void addBook(Book book) {
        // Tu mogłaby być walidacja biznesowa, np. sprawdzenie czy rok nie jest z przyszłości
        bookDAO.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> findBooksByAuthor(String author) {
        return getAllBooks().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int getTotalBooks() {
        return getAllBooks().size();
    }

    @Transactional(readOnly = true)
    public Book getBookByTitle(String title) {
        return bookDAO.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("Książka o tytule: " + title + " nie została znaleziona."));
    }

    @Transactional
    public void updateBook(Book book) {
        // Sprawdzenie czy książka istnieje przed aktualizacją
        // To dodatkowe zabezpieczenie logiczne
        getBookByTitle(book.getTitle());
        bookDAO.update(book);
    }

    @Transactional
    public void deleteBook(String title) {
        bookDAO.deleteByTitle(title);
    }
}

