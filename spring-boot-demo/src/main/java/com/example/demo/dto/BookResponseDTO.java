package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookResponseDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String publisher;
    private List<String> authorNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookResponseDTO() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public List<String> getAuthorNames() { return authorNames; }
    public void setAuthorNames(List<String> authorNames) { this.authorNames = authorNames; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}