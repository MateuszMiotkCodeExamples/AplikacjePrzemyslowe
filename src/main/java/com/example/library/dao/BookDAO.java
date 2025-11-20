package com.example.library.dao;

import com.example.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void save(Book book);
    List<Book> findAll();
    Optional<Book> findByTitle(String title);
    void update(Book book);
    void deleteByTitle(String title);
    List<Book> findByIds(List<Long> ids);
}

