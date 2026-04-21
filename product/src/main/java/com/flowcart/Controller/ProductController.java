package com.flowcart.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.flowcart.Entity.Product;
import com.flowcart.Service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class ProductController {
    private ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }
}
