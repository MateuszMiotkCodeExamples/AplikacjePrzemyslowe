package com.example.library.dto;

public class BookViewDTO {
    private String title;
    private String author;
    private int year;
    private boolean hasCover;
    private String coverUrl;

    public BookViewDTO() {
    }

    public BookViewDTO(String title, String author, int year, boolean hasCover, String coverUrl) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.hasCover = hasCover;
        this.coverUrl = coverUrl;
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

    public boolean isHasCover() {
        return hasCover;
    }

    public void setHasCover(boolean hasCover) {
        this.hasCover = hasCover;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}


