package org.example.dscommerce.repositories

import org.example.dscommerce.entities.Product
import org.springframework.data.jpa.repository.JpaRepository


interface ProductRepository : JpaRepository<Product, Long>