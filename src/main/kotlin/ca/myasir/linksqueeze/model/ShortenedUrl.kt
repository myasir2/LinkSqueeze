package ca.myasir.linksqueeze.model

import java.time.ZonedDateTime

data class ShortenedUrl(

    // Nullable for when the entity is not yet saved
    val id: Int? = null,
    val url: String,
    val userId: Int,
    val expiryDate: ZonedDateTime?
)
