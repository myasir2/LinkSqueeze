package ca.myasir.linksqueeze.model

import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import java.time.ZonedDateTime

data class ShortenedUrl(
    val urlHash: UrlHash,
    val url: String,
    val userId: UserId? = null,
    val expiryDate: ZonedDateTime? = null,
)
