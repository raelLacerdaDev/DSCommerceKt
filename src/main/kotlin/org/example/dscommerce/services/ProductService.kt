package org.example.dscommerce.services

import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.repositories.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun findById(id: Long): ProductDTO {
        val result = productRepository.findById(id)
        val product = result.get()
        val productDTO = ProductDTO(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            imgUrl = product.imgUrl,
        )
        return productDTO
    }

}