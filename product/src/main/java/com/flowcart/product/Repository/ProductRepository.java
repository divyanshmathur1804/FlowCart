package com.flowcart.product.Repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.flowcart.product.Entity.Product;

@Repository
public interface ProductRepository extends org.springframework.data.jpa.repository.JpaRepository<com.flowcart.product.Entity.Product, Integer> {
    public Product findByName(String name);
    public Product findById(long id);
    public void deleteById(long id);
    public List<Product> findAll();
    
}
