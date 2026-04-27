package org.example.dscommerce.dto


data class ProductDTO(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    val imgUrl: String,
)
