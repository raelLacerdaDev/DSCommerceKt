package org.example.dscommerce.controllers


import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.services.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/products")
class ProductController (private val service: ProductService) {

    @GetMapping( "/{id}")
    fun findById(@PathVariable id: Long) : ProductDTO {
        return service.findById(id)
    }

}