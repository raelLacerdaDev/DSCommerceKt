package org.example.dscommerce.services

import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.mappers.toDTO
import org.example.dscommerce.repositories.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun findById(id: Long): ProductDTO {
        val result = productRepository.findById(id)
        val product = result.get()
        val productDTO = product.toDTO()
        return productDTO
    }


    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ProductDTO> {
        val result = productRepository.findAll(pageable)
        return result.map { it.toDTO() }
    }

}