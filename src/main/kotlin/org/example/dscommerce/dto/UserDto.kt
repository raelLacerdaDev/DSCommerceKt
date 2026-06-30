package org.example.dscommerce.dto

import org.example.dscommerce.entities.User
import java.time.LocalDate

data class UserDto(
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phone: String,
    val birthDate: LocalDate,
    val roles: List<String>
){
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        email = user.email,
        phone = user.phone,
        birthDate = user.birthDate,
        roles = user.roles.map { it.authority }
    )
}