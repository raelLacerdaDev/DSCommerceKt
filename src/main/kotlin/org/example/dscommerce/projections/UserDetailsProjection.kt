package org.example.dscommerce.projections

import java.time.LocalDate

interface UserDetailsProjection {
    val name: String
    val username: String
    val phone: String
    val birthDate: LocalDate
    val password: String
    val roleId : Long
    val authority : String
}