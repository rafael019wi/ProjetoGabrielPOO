package org.example.entity;

import java.time.LocalDateTime;

public class Category {
    private Integer id;
    private Integer createdBy;
    private String name;
    private LocalDateTime createdAt;

    public Category(Integer id, Integer createdBy, String name) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
    }

    public Category(Integer id, Integer createdBy, String name, LocalDateTime createdAt) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.createdAt= createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
}
