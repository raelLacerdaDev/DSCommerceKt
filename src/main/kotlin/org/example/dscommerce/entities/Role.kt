package org.example.dscommerce.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "tb_role")
class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    private val authority: String,

    @ManyToMany(mappedBy = "_roles")
    private val _users: MutableSet<User> = mutableSetOf(),
): GrantedAuthority {

    val users: List<User>
        get() = _users.toList()

    override fun getAuthority(): String {
        return authority
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        return authority == other.authority
    }

    override fun hashCode(): Int {
        return authority.hashCode()
    }
}