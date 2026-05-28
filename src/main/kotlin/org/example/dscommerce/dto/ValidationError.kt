package org.example.dscommerce.dto

import java.time.Instant

class ValidationError(
     timestamp: Instant,
     status : Int,
     error: String,
     path: String
) : CustomError(timestamp, status, error, path) {

    private val _errors: MutableList<FieldMessage> = mutableListOf()
    val errors : List<FieldMessage> get() = _errors.toList()


    fun addError(error: FieldMessage) {
        _errors.add(error)
    }
    fun removeError(error: FieldMessage) {
        _errors.remove(error)
    }

}