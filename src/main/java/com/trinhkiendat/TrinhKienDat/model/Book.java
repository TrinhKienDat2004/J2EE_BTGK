package com.trinhkiendat.TrinhKienDat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;

    @NotBlank(message = "Author must not be blank")
    @Size(min = 1, max = 50, message = "Author must be between 1 and 50 characters")
    private String author;

    @Min(value = 1000, message = "Price must be at least 1,000 VND")
    private int price;


    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Invalid Category ID")
    private Category category;

    public Book() {}

    public Book(String title, String author, int price, Category category) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
    }

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
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
}
