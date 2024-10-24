package ca.myasir.linksqueeze.bo

import ca.myasir.linksqueeze.dao.MetricType
import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.dao.UrlMetricsDao
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.service.HashService
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

/**
 * This class will handle all the business logic required for shortening urls, and managing them.
 */
@Service
class UrlBo(
    private val hashService: HashService,
    private val dao: ShortenedUrlDao,
    private val urlMetricsDao: UrlMetricsDao,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Get url for the given hashId and add metric count
     */
    fun getUrl(urlHash: UrlHash): ShortenedUrl? {
        return dao.get(urlHash)?.also {
            urlMetricsDao.addMetric(UrlMetric(
                it.urlHash,
                MetricType.COUNT,
                1.0,
                ZonedDateTime.now(),
            ))
        }
    }

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
     * Delete the given url hash for the given user
     */
    fun deleteUrl(urlHash: UrlHash, userId: UserId) {
        logger.info { "Deleting url: $urlHash" }

        dao.delete(urlHash, userId)
    }

    private companion object {
        const val SHORTENED_URL_LENGTH = 6
    }
}
