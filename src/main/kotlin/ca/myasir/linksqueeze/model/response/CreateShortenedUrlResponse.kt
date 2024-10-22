package ca.myasir.linksqueeze.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class CreateShortenedUrlResponse(

    @JsonProperty("shortenedUrl")
    val shortenedUrl: String,

    @JsonProperty("expiryDate")
    val expiryDate: ZonedDateTime?
)
