package com.flowcart.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flowcart.Entity.Product;
import com.flowcart.Repository.ProductRepository;

@Service
public class ProductService {
    private ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id);
    }
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }
    public void deleteProductById(int id) {
        productRepository.deleteById(id);
    }
    public void updateProductById(int id, String name, int price, int stock) {
        productRepository.updateById(id, name, price, stock);
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
}
