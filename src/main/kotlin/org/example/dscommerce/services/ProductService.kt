package org.example.dscommerce.services


import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.entities.Product
import org.example.dscommerce.mappers.toDTO
import org.example.dscommerce.mappers.toEntity
import org.example.dscommerce.repositories.ProductRepository
import org.example.dscommerce.services.exceptions.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun findById(id: Long): ProductDTO {
        val product = productRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Product with ID $id not found")
        }
        return product.toDTO()
    }


    @Transactional(readOnly = true)
    fun findAll(name: String, pageable: Pageable): Page<ProductDTO> {
        return productRepository.searchByName(name, pageable)
    }


    @Transactional
    fun insert(productDTO: ProductDTO): ProductDTO {
        val newProduct = productDTO.toEntity()
        val result = productRepository.save(newProduct)
        return result.toDTO()
    }

    @Transactional
    fun update(id: Long, productDTO: ProductDTO): ProductDTO {
        val product = productRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Product with ID $id not found")
        val updatedProduct =
            Product(product.id, productDTO.name, productDTO.description, productDTO.price, productDTO.imgUrl)
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toDTO()
    }

    @Transactional
    fun delete(id: Long) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product with ID $id not found")
        }
        productRepository.deleteById(id)
    }

}