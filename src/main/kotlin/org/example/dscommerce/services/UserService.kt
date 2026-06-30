package org.example.dscommerce.services

import org.example.dscommerce.dto.UserDto
import org.example.dscommerce.entities.Role
import org.example.dscommerce.entities.User
import org.example.dscommerce.repositories.UserRepository
import org.example.dscommerce.services.exceptions.UserNotFoundException
import org.springframework.security.core.context.SecurityContextHolder

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val repository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val result = repository.searchByEmail(username)
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
    private fun authenticated() : User {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwt = authentication?.principal as Jwt
        val email = jwt.getClaim<String>("username")
        val user = repository.findByEmail(email)
        return user.orElseThrow { UserNotFoundException("User not found!") }
    }

    @Transactional(readOnly = true)
    fun getMe () : UserDto {
        val user = authenticated()
        return UserDto(user)
    }

}