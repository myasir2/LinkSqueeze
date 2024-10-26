package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.UrlDetails
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.*
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/")
class UrlController(
    private val appConfig: AppConfig,
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

    @GetMapping("/user/urls")
    fun getUserSavedUrls(): ResponseEntity<GetUserSavedUrlsResponse> {
        val urls = urlBo.getUserSavedUrls(UserId("userId"))
        val savedUrls = urls.map {
            UrlDetails(
                originalUrl = it.url,
                shortenedUrl = createFullUrl(it.urlHash),
                expiry = it.expiryDate
            )
        }

        return ResponseEntity.ok(
            GetUserSavedUrlsResponse(savedUrls)
        )
    }

    @GetMapping("/metrics/{hashId}")
    fun getUrlMetrics(@Valid request: GetUrlMetricsRequest): ResponseEntity<GetUrlMetricsResponse> {
        val urlMetrics = urlBo.getUrlMetrics(UrlHash(request.hashId))

        return ResponseEntity.ok(
            GetUrlMetricsResponse(urlMetrics)
        )
    }

    @PostMapping("/generate")
    fun createUrl(@Valid @RequestBody request: CreateShortenedUrlRequest): ResponseEntity<CreateShortenedUrlResponse> {
        val shortenedUrl = urlBo.createShortenedUrl(request.url, UserId("userId"), request.expiry)
        val urlDetails = UrlDetails(
            originalUrl = shortenedUrl.url,
            shortenedUrl = createFullUrl(shortenedUrl.urlHash),
            expiry = shortenedUrl.expiryDate
        )

        return ResponseEntity.ok(
            CreateShortenedUrlResponse(urlDetails)
        )
    }

    @DeleteMapping("/{hashId}")
    fun deleteUrl(@Valid request: DeleteUrlRequest): ResponseEntity<Void> {
        urlBo.deleteUrl(UrlHash(request.hashId), UserId("userId"))

        return ResponseEntity.ok().build()
    }

    private fun createFullUrl(hash: UrlHash): String {
        return "${appConfig.siteUrl}/${hash.value}"
    }
}
