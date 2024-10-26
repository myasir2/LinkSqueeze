package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.UrlDetails
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import ca.myasir.linksqueeze.model.response.RedirectToUrlRequest
import ca.myasir.linksqueeze.util.Constants.GENERATE_URL_ROUTE
import ca.myasir.linksqueeze.util.Constants.URL_REDIRECT_ROUTE
import ca.myasir.linksqueeze.util.UrlHash
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping
class UrlController(
    appConfig: AppConfig,
    private val urlBo: UrlBo,
) : BaseController(appConfig) {
    @GetMapping("$URL_REDIRECT_ROUTE{hashId}")
    fun redirectToUrl(
        @Valid request: RedirectToUrlRequest,
    ): RedirectView {
        val shortenedUrl =
            urlBo.getUrl(UrlHash(request.hashId))
                ?: throw ResourceNotFoundException("URL not found or is expired")

        val redirectView = RedirectView()
        redirectView.url = shortenedUrl.url

        return redirectView
    }

    @PostMapping(GENERATE_URL_ROUTE)
    fun createUrl(
        @Valid @RequestBody request: CreateShortenedUrlRequest,
    ): ResponseEntity<CreateShortenedUrlResponse> {
        val shortenedUrl = urlBo.createShortenedUrl(request.url, null, request.expiry)
        val urlDetails =
            UrlDetails(
                originalUrl = shortenedUrl.url,
                shortenedUrl = createFullUrl(shortenedUrl.urlHash),
                expiry = shortenedUrl.expiryDate,
            )

        return ResponseEntity.ok(
            CreateShortenedUrlResponse(urlDetails),
        )
    }
}
