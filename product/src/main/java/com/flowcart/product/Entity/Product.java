package com.flowcart.product.Entity;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product implements Serializable  { // using Serializable to convert the object into a byte stream and send it to the kafka topic

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // ✅ FIXED

    private String name;

    private Double price;   // ✅ correct
    private Long stock;  // ✅ correct

    public Product() {
    }

    public Product(Long id, String name, Double price, Long stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {   // ✅ FIXED
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {   // ✅ FIXED
        this.stock = stock;
    }
}