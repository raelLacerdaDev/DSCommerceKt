package org.example.dscommerce.controllers


import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.services.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping("/products")
class ProductController (private val service: ProductService) {

    @GetMapping( "/{id}")
    fun findById(@PathVariable id: Long) : ResponseEntity<ProductDTO> {
        val dto = service.findById(id)
        return ResponseEntity.ok(dto)
    }

    @GetMapping
    fun findAll(pageable: Pageable) : ResponseEntity<Page<ProductDTO>> {
        val page = service.findAll(pageable)
        return ResponseEntity.ok(page)
    }


    @PostMapping
    fun insert(@RequestBody product: ProductDTO) : ResponseEntity<ProductDTO> {
        val dto = service.insert(product)

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(dto.id)
            .toUri()


        return ResponseEntity.created(uri).body(dto)
    }



}