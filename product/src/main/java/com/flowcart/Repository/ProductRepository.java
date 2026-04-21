package com.flowcart.Repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.flowcart.Entity.Product;

@Repository
public interface ProductRepository extends org.springframework.data.jpa.repository.JpaRepository<com.flowcart.Entity.Product, Integer> {
    public Product findByName(String name);
    public Product findById(int id);
    public void deleteById(int id);
    public void updateById(int id, String name, int price, int stock);
    public void updateStockById(int id, int stock);
    public void updatePriceById(int id, int price);
    public void updateNameById(int id, String name);
    public List<Product> findAll();
    
}
