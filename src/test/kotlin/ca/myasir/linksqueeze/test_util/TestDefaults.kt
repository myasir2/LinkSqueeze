package ca.myasir.linksqueeze.test_util

import ca.myasir.linksqueeze.dao.MetricType
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.UrlDetails
import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

object TestDefaults {

    const val TEST_URL = "https://myasir.ca"
    val TEST_USER_ID = UserId("userId")
    val TEST_URL_HASH = UrlHash("hash")
    val TEST_EXPIRY: ZonedDateTime = Instant.EPOCH.atZone(ZoneOffset.UTC)
    val TEST_URL_METRIC_TYPE = MetricType.COUNT

    fun createSampleShortenedUrl(
        url: String = TEST_URL,
        urlHash: UrlHash = TEST_URL_HASH,
        userId: UserId? = TEST_USER_ID,
        expiryDate: ZonedDateTime? = TEST_EXPIRY,
    ): ShortenedUrl {
        return ShortenedUrl(
            urlHash, url, userId, expiryDate
        )
    }

    fun createSampleUrlMetric(
        urlHash: UrlHash = TEST_URL_HASH,
        metricType: MetricType = TEST_URL_METRIC_TYPE,
        count: Double = 1.0,
        date: ZonedDateTime = ZonedDateTime.now(),
    ): UrlMetric {
        return UrlMetric(
            urlHash = urlHash,
            metricType = metricType,
            count = count,
            date = date,
        )
    }

    fun createSampleUrlDetails(
        originalUrl: String = TEST_URL,
        shortenedUrl: String = "${TEST_URL}/${TEST_URL_HASH.value}",
        expiry: ZonedDateTime? = TEST_EXPIRY,
    ): UrlDetails {
        return UrlDetails(
            originalUrl = originalUrl,
            shortenedUrl = shortenedUrl,
            expiry = expiry,
        )
    }
}
