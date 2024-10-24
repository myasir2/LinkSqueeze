package ca.myasir.linksqueeze.test_util

import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

object TestDefaults {

    const val TEST_URL = "https://myasir.ca"
    val TEST_USER_ID = UserId("userId")
    val URL_HASH = UrlHash("hash")
    val TEST_EXPIRY: ZonedDateTime = Instant.EPOCH.atZone(ZoneOffset.UTC)

    fun createSampleShortenedUrl(
        url: String = TEST_URL,
        urlHash: UrlHash = URL_HASH,
        userId: UserId? = TEST_USER_ID,
        expiryDate: ZonedDateTime? = TEST_EXPIRY,
    ): ShortenedUrl {
        return ShortenedUrl(
            urlHash, url, userId, expiryDate
        )
    }
}
