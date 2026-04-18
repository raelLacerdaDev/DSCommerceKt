package org.example.dscommerce.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table



@Entity
@Table(name = "tb_product")
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String,
    @Column(columnDefinition = "TEXT")
    val description: String,
    val price: Double,
    val imgUrl: String,
    @ManyToMany
    @JoinTable(
        name = "tb_product_category",
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    private val _categories: MutableSet<Category> = mutableSetOf(),
    @OneToMany(mappedBy = "id.product")
    private val _items: MutableSet<OrderItem> = mutableSetOf(),
) {

    val categories: List<Category>
        get() = _categories.toList()

    val items: List<OrderItem>
        get() = _items.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if(other !is Product) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun addCategory(category: Category) {
        this._categories.add(category)
    }

    fun removeCategory(category: Category) {
        this._categories.remove(category)
    }

}