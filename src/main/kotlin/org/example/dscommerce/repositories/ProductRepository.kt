package org.example.dscommerce.repositories

import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.entities.Product
import org.springframework.data.domain.Page

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface ProductRepository : JpaRepository<Product, Long> {

    @Query(
        """
                SELECT obj
                FROM Product obj
                WHERE obj.name LIKE UPPER(CONCAT('%', :name, '%')) 
            """
    )
    fun searchByName(name: String, pageable: Pageable): Page<ProductDTO>
}