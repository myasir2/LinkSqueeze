package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import ca.myasir.linksqueeze.model.response.DeleteUrlRequest
import ca.myasir.linksqueeze.model.response.RedirectToUrlRequest
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.time.ZonedDateTime

@RestController
@RequestMapping("/")
class UrlController(
    private val urlBo: UrlBo
): BaseController() {

    @GetMapping("{hashId}")
    fun redirectToUrl(@Valid request: RedirectToUrlRequest): RedirectView {
        val shortenedUrl = urlBo.getUrl(UrlHash(request.hashId))
            ?: throw ResourceNotFoundException("URL not found or is expired")

        val redirectView = RedirectView()
        redirectView.url = shortenedUrl.url

        return redirectView
    }

    @PostMapping("/generate")
    fun createUrl(@Valid @RequestBody request: CreateShortenedUrlRequest): ResponseEntity<CreateShortenedUrlResponse> {
        val hash = urlBo.createShortenedUrl(request.url, UserId("userId"), request.expiry)

        return ResponseEntity.ok(
            CreateShortenedUrlResponse(hash.value, request.expiry)
        )
    }

    @DeleteMapping("/{hashId}")
    fun deleteUrl(@Valid request: DeleteUrlRequest): ResponseEntity<Void> {
        urlBo.deleteUrl(UrlHash(request.hashId), UserId("userId"))

        return ResponseEntity.ok().build()
    }
}
