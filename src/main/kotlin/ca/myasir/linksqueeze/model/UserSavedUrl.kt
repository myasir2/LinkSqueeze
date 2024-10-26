package ca.myasir.linksqueeze.model

import java.time.ZonedDateTime

data class UserSavedUrl(

    val url: String,
    val urlHash: String,
    val expiry: ZonedDateTime? = null,
)
