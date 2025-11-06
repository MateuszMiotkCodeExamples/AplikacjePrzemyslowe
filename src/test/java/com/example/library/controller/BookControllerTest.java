package com.example.library.controller;

import com.example.library.dto.CreateBookRequest;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.CsvImportService;
import com.example.library.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BookService bookService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private CsvImportService csvImportService;

    @Test
    void shouldReturnAllBooks() throws Exception {
        // Przygotowanie - programowanie mocka co ma zwrócić
        List<Book> testBooks = new ArrayList<>();
        testBooks.add(new Book("1984", "George Orwell", 1949));
        testBooks.add(new Book("Brave New World", "Aldous Huxley", 1932));
        
        when(bookService.getAllBooks()).thenReturn(testBooks);
        
        // Wykonanie i weryfikacja
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("1984"))
            .andExpect(jsonPath("$[0].author").value("George Orwell"))
            .andExpect(jsonPath("$[1].title").value("Brave New World"));
        
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void shouldCreateNewBook() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("The Hobbit");
        request.setAuthor("J.R.R. Tolkien");
        request.setYear(1937);
        
        // Konwersja obiektu na JSON
        String requestJson = objectMapper.writeValueAsString(request);
        
        // Mock nie musi zwracać nic bo addBook to void
        doNothing().when(bookService).addBook(any(Book.class));
        
        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("The Hobbit"));
        
        verify(bookService).addBook(any(Book.class));
    }

    @Test
    void shouldRejectEmptyTitle() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("");
        request.setAuthor("Author");
        request.setYear(2000);
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(requestJson))
            .andExpect(status().isBadRequest());
        
        // Sprawdzenie że serwis NIE został wywołany
        verify(bookService, never()).addBook(any(Book.class));
    }

    @Test
    void shouldFindBooksByAuthor() throws Exception {
        List<Book> orwellBooks = new ArrayList<>();
        orwellBooks.add(new Book("1984", "George Orwell", 1949));
        
        when(bookService.findBooksByAuthor("George Orwell"))
            .thenReturn(orwellBooks);
        
        mockMvc.perform(get("/api/books/author/{name}", "George Orwell"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].author").value("George Orwell"));
    }

    @Test
    void shouldReturnNotFoundWhenNoBooksFound() throws Exception {
        when(bookService.findBooksByAuthor("Unknown Author"))
            .thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/books/author/{name}", "Unknown Author"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBookAndReturnCorrectResponse() throws Exception {
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("The Hobbit");
        request.setAuthor("Tolkien");
        request.setYear(1937);
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        doNothing().when(bookService).addBook(any(Book.class));
        
        mockMvc.perform(post("/api/books")
                .contentType("application/json")
                .content(requestJson))
            // Asercje - sprawdzany jest WYNIK (odpowiedź HTTP)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("The Hobbit"))
            .andExpect(jsonPath("$.author").value("Tolkien"))
            .andExpect(jsonPath("$.year").value(1937));
        
        // Verify - sprawdzane jest ZACHOWANIE (wywołanie serwisu)
        verify(bookService).addBook(any(Book.class));
    }
}

