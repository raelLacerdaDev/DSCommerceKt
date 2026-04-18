package org.example.dscommerce.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity
@Table(name = "tb_order")
class Order @OptIn(ExperimentalTime::class) constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    val moment: Instant,
    val status: OrderStatus,

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    val client: User,

    @OneToOne(cascade = [CascadeType.ALL])
    val payment: Payment?,

    @OneToMany(mappedBy = "id.order")
    private val _items: MutableSet<OrderItem> = mutableSetOf(),
) {

    val items: List<OrderItem>
        get() = _items.toList()

    val products: List<Product>
        get() = _items.map {
            it.product
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false
        return other.id == this.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}