package org.example.dscommerce.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "tb_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phone: String,
    val birthDate: LocalDate,
    val password: String,
    @OneToMany(mappedBy = "client")
    private val _orders: MutableList<Order> = mutableListOf(),
) {
    val orders : List<Order>
        get() = _orders.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return other.id == this.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun addOrder(order: Order) {
        this._orders.add(order)
    }

    fun removeOrder(order: Order) {
        this._orders.remove(order)
    }
}