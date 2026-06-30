package org.example.dscommerce.controllers

import org.example.dscommerce.dto.UserDto
import org.example.dscommerce.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    fun findMe() : ResponseEntity<UserDto> {
        val user = userService.getMe()
        return ResponseEntity.ok().body(user)
    }
}