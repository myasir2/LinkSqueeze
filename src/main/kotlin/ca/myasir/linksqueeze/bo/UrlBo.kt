package ca.myasir.linksqueeze.bo

import ca.myasir.auroraweatherservice.util.toIsoFormat
import ca.myasir.linksqueeze.service.HashService
import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.exp

/**
 * This class will handle all the business logic required for shortening urls, and managing them.
 */
@Service
class UrlBo(
    private val hashService: HashService,
    private val dao: ShortenedUrlDao
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Create a shortened URL, insert into database with max expiry date, and return its hash (i.e. shortened id)
     */
    fun createShortenedUrl(url: String, userId: UserId?, expiry: ZonedDateTime? = null): UrlHash {
        logger.info { "Creating shortened url: $url by $userId" }

        val urlHash = hashService.createUniqueHash(url, SHORTENED_URL_LENGTH)
        val shortenedUrl = ShortenedUrl(
            urlHash = urlHash,
            url = url,
            userId = userId,
            expiryDate = expiry
        )
        dao.add(shortenedUrl)

        return urlHash
    }

    /**
     * Get a list of the saved urls created by the given user
     */
    fun getUserSavedUrls(userId: UserId): List<ShortenedUrl> {
        logger.info { "Getting saved urls for: $userId" }

        val urls = dao.getByUser(userId)

        logger.info { "Saved urls for $userId: $urls" }

        return urls
    }

    /**
     * Delete the given url hash
     */
    fun deleteUrl(urlHash: UrlHash) {
        logger.info { "Deleting url: $urlHash" }

        dao.delete(urlHash)
    }

    private companion object {
        const val MAX_EXPIRY_DAYS = 30L
        const val SHORTENED_URL_LENGTH = 6
    }
}
