package org.example.dscommerce.dto

import java.time.Instant


data class CustomError(
    val timestamp: Instant,
    val status : Int,
    val error: String,
    val path: String
)
