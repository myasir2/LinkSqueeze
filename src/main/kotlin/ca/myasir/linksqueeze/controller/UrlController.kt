package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("/")
class UrlController(
    private val urlBo: UrlBo
): BaseController() {

    private val logger = KotlinLogging.logger {}

    @PostMapping("/url")
    fun createUrl(@Valid @RequestBody request: CreateShortenedUrlRequest): ResponseEntity<CreateShortenedUrlResponse> {
        logger.info { "Create shortened URL for ${request.url}" }

        val hash = urlBo.createShortenedUrl(request.url, null, request.expiry)

        return ResponseEntity.ok(
            CreateShortenedUrlResponse(hash.value, request.expiry)
        )
    }
}
