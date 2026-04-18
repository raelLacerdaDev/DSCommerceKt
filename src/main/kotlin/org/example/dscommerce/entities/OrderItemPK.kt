package org.example.dscommerce.entities

import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Embeddable
class OrderItemPK(
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order? = null,
    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: Product? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItemPK) return false
        if (order != other.order) return false
        if (product != other.product) return false
        return true
    }

    override fun hashCode(): Int {
        var result = order?.hashCode() ?: 0
        result = 31 * result + (product?.hashCode() ?: 0)
        return result
    }
}