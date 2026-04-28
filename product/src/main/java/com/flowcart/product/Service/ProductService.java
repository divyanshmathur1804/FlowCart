package com.flowcart.product.Service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.flowcart.product.Entity.Product;
import com.flowcart.product.Events.OrderEvents;
import com.flowcart.product.ExceptionHandler.ProductNotFoundException;
import com.flowcart.product.Repository.ProductRepository;

// import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    //private EntityManager em;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        // this.em = em;
    }

    @Transactional // Ensure that the method runs within a transaction
    public Product createProduct(Product product) {
        // Product p = new Product();
        // p.setName(product.getName());
        // p.setPrice(product.getPrice());
        // p.setStock(product.getStock());
        // em.persist(p);
        // return p; all this can be done by save method of JpaRepository
        return productRepository.save(product);

    }

    @KafkaListener(topics = "order-events", groupId = "product-group")
    public void consume(OrderEvents event) {
        System.out.println("🔥 EVENT RECEIVED: " + event);

        Product product = productRepository.findById(event.getProductId()).orElseThrow();

        product.setStock(product.getStock() - event.getQuantity());

        productRepository.save(product);
    } // This method listens to the Kafka topic "order-events" and updates the stock of the product when an order is created



    @Cacheable(value = "products", key = "#id")
    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }
    // public Product getProductByName(String name) {
    //     return productRepository.findBy(name, nul);
    // }

    @Transactional
    public void deleteProductById(long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product updateProduct(long id, Product updatedProduct) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

    product.setName(updatedProduct.getName());
    product.setPrice(updatedProduct.getPrice());
    product.setStock(updatedProduct.getStock());

    return productRepository.save(product);

    // Alternatively, you can use the EntityManager to update the product
    // Product product = em.find(Product.class, id);
    // product.setName(updatedProduct.getName());
    // product.setPrice(updatedProduct.getPrice());
    // product.setStock(updatedProduct.getStock());
    // em.merge(product);
    // return product;
}
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
}
