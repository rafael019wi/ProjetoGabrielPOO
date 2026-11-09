package org.example.entity;

import java.time.LocalDateTime;

public class Product {
    private Integer id;
    private Integer idCategory;
    private Integer createdBy;
    private String creatorName;
    private String bookAuthor;
    private String nameProduct;
    private String description;
    private Double price;
    private Integer stock;
    private String status;
    private LocalDateTime createdAt;

    public Product(Integer id, Integer idCategory) {
        this.id = id;
        this.idCategory = idCategory;
        this.createdBy = createdBy;
        this.bookAuthor = bookAuthor;
        this.nameProduct = nameProduct;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public Product(Integer id, Integer idCategory, Integer createdBy, String bookAuthor, String nameProduct, String description, Double price, Integer stock, String status, LocalDateTime createdAt) {
        this.id = id;
        this.idCategory = idCategory;
        this.createdBy = createdBy;
        this.bookAuthor = bookAuthor;
        this.nameProduct = nameProduct;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Product(Integer id, Integer idCategory, Integer createdBy, String creatorName, String bookAuthor, String nameProduct, String description, Double price, Integer stock, String status, LocalDateTime createdAt) {
        this.id = id;
        this.idCategory = idCategory;
        this.createdBy = createdBy;
        this.creatorName = creatorName;
        this.bookAuthor = bookAuthor;
        this.nameProduct = nameProduct;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
