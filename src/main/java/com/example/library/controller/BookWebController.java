package com.example.library.controller;

import com.example.library.dto.BookViewDTO;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web/books")
public class BookWebController {

    private final BookService bookService;
    private final FileStorageService fileStorageService;

    public BookWebController(BookService bookService, FileStorageService fileStorageService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        List<BookViewDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            bookDTOs.add(convertToViewDTO(book));
        }
        model.addAttribute("books", bookDTOs);
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new BookViewDTO("", "", 0, false, null));
        return "books/form";
    }

    @PostMapping
    public String createBook(@RequestParam("title") String title,
                             @RequestParam("author") String author,
                             @RequestParam("year") int year,
                             @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                             RedirectAttributes redirectAttributes) {
        if (title == null || title.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tytuł książki jest wymagany");
            return "redirect:/web/books/new";
        }

        Book book = new Book(title, author, year);

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                String filename = fileStorageService.storeFile(coverFile, title);
                book.setCoverImageFilename(filename);
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute("error",
                        "Błąd podczas zapisywania okładki: " + ex.getMessage());
                return "redirect:/web/books/new";
            }
        }

        bookService.addBook(book);
        redirectAttributes.addFlashAttribute("success", "Książka została dodana pomyślnie");
        return "redirect:/web/books";
    }

    @GetMapping("/{title}")
    public String showBookDetails(@PathVariable String title, Model model) {
        List<Book> books = bookService.getAllBooks();
        Book book = books.stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        if (book == null) {
            return "redirect:/web/books";
        }

        model.addAttribute("book", convertToViewDTO(book));
        return "books/details";
    }

    private BookViewDTO convertToViewDTO(Book book) {
        boolean hasCover = book.getCoverImageFilename() != null;
        String coverUrl = null;
        if (hasCover) {
            coverUrl = "/api/books/" + book.getTitle() + "/cover";
        }
        return new BookViewDTO(
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                hasCover,
                coverUrl
        );
    }
}



