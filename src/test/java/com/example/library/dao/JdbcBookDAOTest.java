package com.example.library.dao;

import com.example.library.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcBookDAO.class)
class JdbcBookDAOTest {

    @Autowired
    private JdbcBookDAO bookDAO;

    @Test
    void shouldSaveBookToDatabase() {
        // Given
        Book book = new Book("Wzorce Projektowe", "Erich Gamma", 1994);

        // When
        bookDAO.save(book);
        Optional<Book> foundBook = bookDAO.findByTitle("Wzorce Projektowe");

        // Then
        assertThat(foundBook).isPresent();
    }

    @Test
    void shouldReturnCorrectDataAfterSave() {
        // Given
        Book book = new Book("Clean Code", "Robert C. Martin", 2008);
        book.setCoverImageFilename("clean_code.jpg");
        bookDAO.save(book);

        // When
        Book result = bookDAO.findByTitle("Clean Code").get();

        // Then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(book);
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingBook() {
        // Given
        String nonExistingTitle = "Harry Potter i Komnata Tajemnic";

        // When
        Optional<Book> result = bookDAO.findByTitle(nonExistingTitle);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateBookAuthor() {
        // Given
        Book book = new Book("Test Book", "Stary Autor", 2000);
        bookDAO.save(book);

        // When
        book.setAuthor("Nowy Autor");
        bookDAO.update(book);

        // Then
        Book updatedBook = bookDAO.findByTitle("Test Book").get();
        assertThat(updatedBook.getAuthor()).isEqualTo("Nowy Autor");
    }

    @Test
    void shouldDeleteBookFromDatabase() {
        // Given
        Book book = new Book("Do usunięcia", "Anonim", 2020);
        bookDAO.save(book);

        // When
        bookDAO.deleteByTitle("Do usunięcia");

        // Then
        Optional<Book> result = bookDAO.findByTitle("Do usunięcia");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGenerateIdOnSave() {
        // Given
        Book book = new Book("Pragmatyczny Programista", "Andy Hunt", 1999);
        assertThat(book.getId()).isNull(); // Przed zapisem ID jest nullem

        // When
        bookDAO.save(book);

        // Then
        assertThat(book.getId()).isNotNull(); // Po zapisie ID musi być nadane
        assertThat(book.getId()).isGreaterThan(0L); // ID musi być dodatnie
    }

    @Test
    void shouldFindBooksByIdsList() {
        // Given - przygotowanie 3 książek
        Book b1 = new Book("Java 1", "Author", 2000);
        Book b2 = new Book("Java 2", "Author", 2002);
        Book b3 = new Book("Java 3", "Author", 2004);

        bookDAO.save(b1);
        bookDAO.save(b2);
        bookDAO.save(b3);

        // When - szukamy tylko pierwszej i trzeciej
        List<Long> idsToFind = List.of(b1.getId(), b3.getId());
        List<Book> foundBooks = bookDAO.findByIds(idsToFind);

        // Then
        assertThat(foundBooks).hasSize(2)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java 1", "Java 3");
    }
}

