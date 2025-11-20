package com.example.library.service;

import com.example.library.dao.BookDAO;
import com.example.library.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDAO bookDAO; // To jest atrapa, nie prawdziwe DAO

    @InjectMocks
    private BookService bookService; // Tutaj Mockito wstrzyknie atrapę DAO

    @Test
    void shouldReturnBookWhenFound() {
        // Given
        String title = "W pustyni i w puszczy";
        Book expectedBook = new Book(title, "Henryk Sienkiewicz", 1911);

        // Konfiguracja atrapy: "Gdy ktoś zapyta o ten tytuł, zwróć tę książkę"
        when(bookDAO.findByTitle(title)).thenReturn(Optional.of(expectedBook));

        // When
        Book result = bookService.getBookByTitle(title);

        // Then
        assertThat(result).isEqualTo(expectedBook);
        // Weryfikacja: czy serwis faktycznie wywołał metodę DAO?
        verify(bookDAO).findByTitle(title);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        String title = "Nieistniejąca książka";

        // Konfiguracja atrapy: "Gdy ktoś zapyta, zwróć puste pudełko"
        when(bookDAO.findByTitle(title)).thenReturn(Optional.empty());

        // When & Then
        // Oczekujemy, że serwis rzuci wyjątek, bo tak zaprogramowaliśmy logikę biznesową
        assertThatThrownBy(() -> bookService.getBookByTitle(title))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nie została znaleziona");
    }

    @Test
    void shouldAddBook() {
        // Given
        Book book = new Book("Test", "Autor", 2022);

        // When
        bookService.addBook(book);

        // Then
        // Sprawdzamy tylko, czy serwis przekazał zadanie do DAO
        verify(bookDAO).save(book);
    }
}

