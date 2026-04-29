package com.flowcart.product.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.flowcart.product.Entity.ProcessedOrder;
import com.flowcart.product.Entity.Product;
import com.flowcart.product.Events.OrderEvents;
import com.flowcart.product.Events.StockResultEvent;
import com.flowcart.product.ExceptionHandler.ProductNotFoundException;
import com.flowcart.product.Repository.ProcessOrderRepository;
import com.flowcart.product.Repository.ProductRepository;

import org.springframework.kafka.core.KafkaTemplate;

// import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private ProcessOrderRepository processOrderRepository;
    @Autowired
    private ObjectMapper objectMapper; // This is used to convert the JSON string into an OrderEvents object

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // This is used to send messages to the Kafka topic
    //private EntityManager em;
    public ProductService(ProductRepository productRepository, ProcessOrderRepository processOrderRepository) {
        this.productRepository = productRepository;
        this.processOrderRepository = processOrderRepository;
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

    @Transactional
@KafkaListener(topics = {
        "order-events",
        "order-events-retry-1",
        "order-events-retry-2"
    }, groupId = "product-group") // Listen to the Kafka topic "order-events" and its retry topics
public void consume(String payload) {

    try {
        OrderEvents event = objectMapper.readValue(payload, OrderEvents.class);

        // ✅ Correct idempotency check
        if (processOrderRepository.existsById(event.getOrderId())) {
            return;
        }

        Product product = productRepository.findById(event.getProductId())
                .orElse(null);

        boolean success = false;

        if (product != null && product.getStock() >= event.getQuantity()) {
            product.setStock(product.getStock() - event.getQuantity());
            productRepository.save(product);
            success = true;
        }

        processOrderRepository.save(
                new ProcessedOrder(event.getOrderId())
        );

        StockResultEvent result = new StockResultEvent(
                event.getOrderId(),
                success
        );

        // (OK for now)
        kafkaTemplate.send("stock-result-events",
                objectMapper.writeValueAsString(result));

    } catch (Exception e) {
        // log properly
        throw new RuntimeException(e); // let Kafka retry
    }
} // This method listens to the Kafka topic "order-events" and updates the stock of the product when an order is created

@KafkaListener(topics = "order-events-dlt", groupId = "product-dlt-group")
public void handleDLT(String payload) {

    System.out.println("🔥 Message moved to DLT: " + payload);

    // store in DB / alert / manual fix
    throw new RuntimeException("Manual intervention needed for payload: " + payload);
}



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
