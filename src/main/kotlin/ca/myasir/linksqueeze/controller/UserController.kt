package ca.myasir.linksqueeze.controller

import ca.myasir.linksqueeze.bo.UrlBo
import ca.myasir.linksqueeze.config.AppConfig
import ca.myasir.linksqueeze.exception.ResourceNotFoundException
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.UrlDetails
import ca.myasir.linksqueeze.model.request.CreateShortenedUrlRequest
import ca.myasir.linksqueeze.model.response.CreateShortenedUrlResponse
import ca.myasir.linksqueeze.model.response.DeleteUrlRequest
import ca.myasir.linksqueeze.model.response.GetUrlMetricsRequest
import ca.myasir.linksqueeze.model.response.GetUrlMetricsResponse
import ca.myasir.linksqueeze.model.response.GetUserSavedUrlsResponse
import ca.myasir.linksqueeze.util.Constants.DELETE_URL_ROUTE
import ca.myasir.linksqueeze.util.Constants.GET_URL_METRICS_ROUTE
import ca.myasir.linksqueeze.util.Constants.USER_GENERATE_URL_ROUTE
import ca.myasir.linksqueeze.util.Constants.USER_SAVED_URLS_ROUTE
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UserController(
    appConfig: AppConfig,
    private val urlBo: UrlBo,
) : BaseController(appConfig) {
    @GetMapping(USER_SAVED_URLS_ROUTE)
    fun getUserSavedUrls(httpServletRequest: HttpServletRequest): ResponseEntity<GetUserSavedUrlsResponse> {
        val context = super.extractContextFromRequest(httpServletRequest)
        val urls = urlBo.getUserSavedUrls(context.userId)
        val savedUrls =
            urls.map {
                UrlDetails(
                    originalUrl = it.url,
                    shortenedUrl = createFullUrl(it.urlHash),
                    expiry = it.expiryDate,
                )
            }

        return ResponseEntity.ok(
            GetUserSavedUrlsResponse(savedUrls),
        )
    }

    @GetMapping("$GET_URL_METRICS_ROUTE/{hashId}")
    fun getUrlMetrics(
        httpServletRequest: HttpServletRequest,
        @Valid request: GetUrlMetricsRequest,
    ): ResponseEntity<GetUrlMetricsResponse> {
        val context = super.extractContextFromRequest(httpServletRequest)
        val url = getUserSavedUrl(UrlHash(request.hashId), context.userId)
        val urlMetrics = urlBo.getUrlMetrics(url.urlHash)

        return ResponseEntity.ok(
            GetUrlMetricsResponse(urlMetrics),
        )
    }

    @PostMapping(USER_GENERATE_URL_ROUTE)
    fun createUrl(
        httpServletRequest: HttpServletRequest,
        @Valid @RequestBody request: CreateShortenedUrlRequest,
    ): ResponseEntity<CreateShortenedUrlResponse> {
        val context = super.extractContextFromRequest(httpServletRequest)
        val shortenedUrl = urlBo.createShortenedUrl(request.url, context.userId, request.expiry)
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

    @DeleteMapping("$DELETE_URL_ROUTE/{hashId}")
    fun deleteUrl(
        httpServletRequest: HttpServletRequest,
        @Valid request: DeleteUrlRequest,
    ): ResponseEntity<Void> {
        val context = super.extractContextFromRequest(httpServletRequest)
        val userId = context.userId
        val urlHash = UrlHash(request.hashId)

        // Ensure URL object exists for the given hash and userId
        getUserSavedUrl(urlHash, userId)

        urlBo.deleteUrl(urlHash, userId)

        return ResponseEntity.ok().build()
    }

    /**
     * It is unlikely that a user will have millions of URLs. Therefore, doing a filter on a list is an acceptable
     * trade-off as likely the user has a handful of generated URLs. If we see that getting a list of URLs is
     * causing bottlenecks, then we can modify the code to get only 1 URL object from the DB. If no URL object is found
     * for the given hash and userId, then throw NotFoundException
     */
    private fun getUserSavedUrl(
        hash: UrlHash,
        userId: UserId,
    ): ShortenedUrl {
        return urlBo.getUserSavedUrls(userId).firstOrNull { it.urlHash == hash }
            ?: throw ResourceNotFoundException("URL not found or is expired")
    }
}
