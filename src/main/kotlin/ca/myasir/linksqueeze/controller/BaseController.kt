package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.response.ExceptionResponse
import ca.myasir.linksqueeze.util.ResponseConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

abstract class BaseController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [(BindException::class)])
    fun handleValidationExceptionForPostRequests(exception: BindException): Map<String, String> {
        val fieldErrors = HashMap<String, String>()

        exception.bindingResult.allErrors.forEach { error ->
            val fieldError = error as? FieldError

            fieldError?.also { field ->
                val message = error.defaultMessage

                fieldErrors[field.field] = message ?: ""
            }
        }

        return fieldErrors
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [(HttpMessageNotReadableException::class)])
    fun handleMissingFieldsExceptionForPostRequests(exception: HttpMessageNotReadableException): ExceptionResponse {
        return ExceptionResponse(
            "There are missing required properties in this request"
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = [(ResourceNotFoundException::class)])
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ExceptionResponse {
        return ExceptionResponse(ResponseConstants.RESOURCE_NOT_FOUND_MESSAGE)
    }
}
