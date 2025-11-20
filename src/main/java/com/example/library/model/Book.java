package com.example.library.model;

public class Book {
    private Long id;
    private String title;
    private String author;
    private int year;
    private String coverImageFilename;

    // Default constructor needed for JDBC
    public Book() {}

    // Constructor needed for creating books
    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCoverImageFilename() {
        return coverImageFilename;
    }

    public void setCoverImageFilename(String coverImageFilename) {
        this.coverImageFilename = coverImageFilename;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", coverImageFilename='" + coverImageFilename + '\'' +
                '}';
    }
}

