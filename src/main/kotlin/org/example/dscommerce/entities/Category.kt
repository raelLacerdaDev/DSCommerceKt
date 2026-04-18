package org.example.dscommerce.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table


@Entity
@Table(name = "tb_category")
class Category (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String,

    @ManyToMany(mappedBy = "_categories")
    private val _products: MutableSet<Product> = mutableSetOf(),
){

    val products: List<Product> get() = _products.toList()

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false
        return this.id == other.id
    }

    fun addProduct(product: Product) {
        _products.add(product)
    }

    fun removeProduct(product: Product) {
        _products.remove(product)
    }

}