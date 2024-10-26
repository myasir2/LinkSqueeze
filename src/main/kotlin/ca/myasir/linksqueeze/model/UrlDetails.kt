package ca.myasir.linksqueeze.model

import java.time.ZonedDateTime

data class UrlDetails(

    val originalUrl: String,
    val shortenedUrl: String,
    val expiry: ZonedDateTime? = null,
)
