package org.example.dscommerce.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size


data class ProductDTO(
    val id: Long? = null,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 3, max = 80, message = "Name must be between 3 and 80")
    val name: String,

    @field:NotBlank(message = "description is required")
    @field:Size(min = 10, message = "description must have at least 10")
    val description: String,

    @field:Positive(message = "Price must be bigger than zero")
    val price: Double,

    val imgUrl: String,
)
