package org.example.dscommerce.services

import org.example.dscommerce.entities.Role
import org.example.dscommerce.entities.User
import org.example.dscommerce.repositories.UserRepository
import org.example.dscommerce.services.exceptions.UserNotFoundException

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserService(private val repository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val result = repository.findByEmail(username)
        if(result.isEmpty()) throw UserNotFoundException("User with username: $username not found!")

        val user = User(
            name = result[0].name,
            email = result[0].username,
            phone =  result[0].phone,
            birthDate = result[0].birthDate,
            password = result[0].password,
        )

        for (data in result) {
            user.addRole(Role(data.roleId, data.authority))
        }

        return user
    }
}