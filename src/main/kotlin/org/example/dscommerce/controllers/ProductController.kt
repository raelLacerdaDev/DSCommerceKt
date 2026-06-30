package org.example.dscommerce.controllers


import jakarta.validation.Valid
import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.services.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    fun findAll(@RequestParam(name = "name", defaultValue = "") name: String, pageable: Pageable) : ResponseEntity<Page<ProductDTO>> {
        val page = service.findAll(name, pageable)
        return ResponseEntity.ok(page)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    fun insert(@Valid @RequestBody product: ProductDTO) : ResponseEntity<ProductDTO> {
        val dto = service.insert(product)

        val uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(dto.id)
            .toUri()


        return ResponseEntity.created(uri).body(dto)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping( "/{id}")
    fun update(@PathVariable id: Long,@Valid @RequestBody product: ProductDTO) : ResponseEntity<ProductDTO> {
        val dto = service.update(id, product)
        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : ResponseEntity<Unit> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }


}