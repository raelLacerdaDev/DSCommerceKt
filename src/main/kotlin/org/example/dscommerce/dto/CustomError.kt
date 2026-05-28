package org.example.dscommerce.dto

import java.time.Instant



// existe um novo padrao ProblemDetail para api rest
open class CustomError(
    val timestamp: Instant,
    val status : Int,
    val error: String,
    val path: String
) {
    override fun toString(): String {
        return "CustomError(timestamp=$timestamp, error='$error', path='$path')"
    }
}
