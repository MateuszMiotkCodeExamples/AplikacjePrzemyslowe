package com.example.library.dto;

public class CreateBookRequest {
    private String title;
    private String author;
    private int year;
    
    // Brak pól jak internalId - system je wygeneruje sam!

    public CreateBookRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

