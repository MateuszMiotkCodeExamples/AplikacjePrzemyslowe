package com.example.library.dto;

public class BookDTO {
    private String title;
    private String author;
    private int year;

    // Pusty konstruktor - Spring potrzebuje go do konwersji z JSON
    public BookDTO() {
    }

    public BookDTO(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    // Gettery i settery - MUSZĄ być dla JSON
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

