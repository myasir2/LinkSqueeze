package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.model.UrlDetails
import com.fasterxml.jackson.annotation.JsonProperty

data class CreateShortenedUrlResponse(
    @JsonProperty("urlDetails")
    val urlDetails: UrlDetails,
)
