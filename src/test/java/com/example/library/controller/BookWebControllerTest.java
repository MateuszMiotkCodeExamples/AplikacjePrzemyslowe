package com.example.library.controller;

import com.example.library.dto.BookViewDTO;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookWebController.class)
class BookWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void shouldDisplayListOfBooksAsDTO() throws Exception {
        List<Book> books = new ArrayList<>();
        Book book1 = new Book("1984", "George Orwell", 1949);
        book1.setCoverImageFilename("cover1.jpg");
        books.add(book1);

        Book book2 = new Book("Brave New World", "Aldous Huxley", 1932);
        books.add(book2);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/web/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(2)))
                .andExpect(model().attribute("books", hasItem(
                        allOf(
                                hasProperty("title", is("1984")),
                                hasProperty("hasCover", is(true)),
                                hasProperty("coverUrl", notNullValue())
                        )
                )))
                .andExpect(model().attribute("books", hasItem(
                        allOf(
                                hasProperty("title", is("Brave New World")),
                                hasProperty("hasCover", is(false)),
                                hasProperty("coverUrl", nullValue())
                        )
                )))
                .andExpect(model().attribute("books", everyItem(instanceOf(BookViewDTO.class))));

        verify(bookService).getAllBooks();
    }

    @Test
    void shouldDisplayEmptyListWhenNoBooksExist() throws Exception {
        when(bookService.getAllBooks()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/web/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attribute("books", hasSize(0)));
    }

    @Test
    void shouldDisplayCreateForm() throws Exception {
        mockMvc.perform(get("/web/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void shouldCreateBookAndRedirect() throws Exception {
        doNothing().when(bookService).addBook(any());

        mockMvc.perform(post("/web/books")
                        .param("title", "New Book")
                        .param("author", "New Author")
                        .param("year", "2025"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/books"))
                .andExpect(flash().attributeExists("success"));

        verify(bookService).addBook(any());
    }

    @Test
    void shouldRejectEmptyTitle() throws Exception {
        mockMvc.perform(post("/web/books")
                        .param("title", "")
                        .param("author", "Author")
                        .param("year", "2025"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/books/new"))
                .andExpect(flash().attributeExists("error"));

        verify(bookService, never()).addBook(any());
    }

    @Test
    void shouldDisplayBookDetailsAsDTO() throws Exception {
        Book book = new Book("Test Book", "Test Author", 2025);
        book.setCoverImageFilename("cover.jpg");
        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/web/books/{title}", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/details"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", instanceOf(BookViewDTO.class)))
                .andExpect(model().attribute("book", allOf(
                        hasProperty("title", is("Test Book")),
                        hasProperty("hasCover", is(true)),
                        hasProperty("coverUrl", is("/api/books/Test Book/cover"))
                )));
    }

    @Test
    void shouldGenerateCorrectCoverUrlInDTO() throws Exception {
        Book book = new Book("Book With Cover", "Author", 2025);
        book.setCoverImageFilename("some-file.jpg");
        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/web/books/{title}", "Book With Cover"))
                .andExpect(model().attribute("book",
                        hasProperty("coverUrl", is("/api/books/Book With Cover/cover"))));
    }
}


