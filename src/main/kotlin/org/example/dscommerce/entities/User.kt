package org.example.dscommerce.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate

@Entity
@Table(name = "tb_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    @Column(unique = true)
    val email: String,
    val phone: String,
    val birthDate: LocalDate,
    private val password: String,
    @OneToMany(mappedBy = "client")
    private val _orders: MutableList<Order> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "tb_user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private val _roles: MutableSet<Role> = mutableSetOf(),
) : UserDetails {
    val orders : List<Order>
        get() = _orders.toList()

    val roles : List<Role>
        get() = _roles.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return other.id == this.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles
    }

    override fun getUsername(): String {
        return email
    }
    override fun getPassword(): String {
        return password
    }

    fun hasRole(name: String): Boolean {
        return _roles.any { it.authority == name }
    }

    fun addRole(role: Role) {
        _roles.add(role)
    }

}