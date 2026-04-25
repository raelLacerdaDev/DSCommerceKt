package org.example.dscommerce.mappers

import org.example.dscommerce.dto.ProductDTO
import org.example.dscommerce.entities.Product


fun Product.toDTO() : ProductDTO = ProductDTO(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    imgUrl = this.imgUrl,
)

