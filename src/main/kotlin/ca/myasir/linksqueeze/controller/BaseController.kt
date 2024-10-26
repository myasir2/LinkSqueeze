package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.context.Context
import ca.myasir.linksqueeze.context.RequestAttributeType
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.response.ExceptionResponse
import ca.myasir.linksqueeze.util.ResponseConstants
import ca.myasir.linksqueeze.util.UrlHash
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

abstract class BaseController(
    private val appConfig: AppConfig,
) {
    private val logger = KotlinLogging.logger {}

    fun extractContextFromRequest(request: HttpServletRequest): Context {
        return request.getAttribute(RequestAttributeType.CONTEXT.toString()) as Context
    }

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
        logger.error { exception.message }

        return ExceptionResponse(
            "There are missing required properties in this request",
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = [(ResourceNotFoundException::class)])
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ExceptionResponse {
        logger.error { exception.message }

        return ExceptionResponse(ResponseConstants.RESOURCE_NOT_FOUND_MESSAGE)
    }

    internal fun createFullUrl(hash: UrlHash): String {
        return "${appConfig.siteUrl}/${hash.value}"
    }
}
