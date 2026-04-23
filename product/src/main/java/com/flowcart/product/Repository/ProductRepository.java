package com.flowcart.product.Repository;



import org.springframework.stereotype.Repository;



@Repository
public interface ProductRepository extends org.springframework.data.jpa.repository.JpaRepository<com.flowcart.product.Entity.Product, Long> {
    
    
}
