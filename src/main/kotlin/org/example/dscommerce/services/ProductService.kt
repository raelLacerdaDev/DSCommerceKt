package org.example.dscommerce.services

import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.entities.Product
import org.example.dscommerce.mappers.toDTO
import org.example.dscommerce.mappers.toEntity
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
        return product.toDTO()
    }


    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ProductDTO> {
        val result = productRepository.findAll(pageable)
        return result.map { it.toDTO() }
    }


    @Transactional fun insert(productDTO: ProductDTO): ProductDTO {
        val newProduct = productDTO.toEntity()
        val result = productRepository.save(newProduct)
        return result.toDTO()
    }

    @Transactional fun update(id: Long, productDTO: ProductDTO): ProductDTO {
        val product = productRepository.getReferenceById(id)
        val updatedProduct = Product(product.id, productDTO.name, productDTO.description, productDTO.price, productDTO.imgUrl)
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toDTO()
    }

    @Transactional fun delete(id: Long) {
        productRepository.deleteById(id)
    }

}