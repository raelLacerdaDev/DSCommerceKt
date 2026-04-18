package org.example.dscommerce.entities

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity

@Entity
class OrderItem(
    order: Order,
    product: Product,
    val quantity: Int,
    val price: Double,
) {
    @EmbeddedId
    private val id: OrderItemPK = OrderItemPK(order, product)

    val order: Order
        get() = id.order ?: throw IllegalStateException("Order not found")

    val product: Product
        get() = id.product ?: throw IllegalStateException("Product not found")
}