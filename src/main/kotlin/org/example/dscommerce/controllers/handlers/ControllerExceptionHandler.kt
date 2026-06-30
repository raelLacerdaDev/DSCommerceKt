package org.example.dscommerce.controllers.handlers

import jakarta.servlet.http.HttpServletRequest
import org.example.dscommerce.dto.CustomError
import org.example.dscommerce.dto.FieldMessage
import org.example.dscommerce.dto.ValidationError
import org.example.dscommerce.services.exceptions.ResourceNotFoundException
import org.example.dscommerce.services.exceptions.UserNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant


@ControllerAdvice
class ControllerExceptionHandler {

    // problemas de negócio crie excecoes personalizadas
    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundHandler(e: ResourceNotFoundException, request: HttpServletRequest) : ResponseEntity<CustomError> {
        val status = HttpStatus.NOT_FOUND.value()
        val err = CustomError(
            timestamp = Instant.now(),
            status = status,
            error = e.message ?: e.localizedMessage,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    // problemas de infra usa a propria excecão
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun dataIntegrityViolationHandler(e: DataIntegrityViolationException, request: HttpServletRequest) : ResponseEntity<CustomError> {
        val status = HttpStatus.BAD_REQUEST.value()
        val err = CustomError(
            timestamp = Instant.now(),
            status = status,
            error = "Database Integrity Violation: This resource cannot be deleted because it is in use.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValid(e: MethodArgumentNotValidException, request: HttpServletRequest) : ResponseEntity<CustomError> {
        val status = HttpStatus.UNPROCESSABLE_ENTITY.value()

        val err = ValidationError(
            timestamp = Instant.now(),
            status = status,
            error = "Invalid data",
            path = request.requestURI
        )
        for (error in e.bindingResult.fieldErrors) {
            val fieldName = error.field
            val fieldMessage = error.defaultMessage ?: ""
            err.addError(FieldMessage(fieldName, fieldMessage))
        }

        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun userNotFoundHandler(e: UserNotFoundException, request: HttpServletRequest) : ResponseEntity<CustomError> {
        val status = HttpStatus.NOT_FOUND.value()
        val err = CustomError(
            timestamp = Instant.now(),
            status = status,
            error = e.message ?: e.localizedMessage,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

}