package ca.myasir.linksqueeze.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateShortenedUrlRequest(

    @JsonProperty("url", required = true)
    @field:Pattern(
        regexp = "^(https?:\\/\\/)?(www\\.)?([a-zA-Z0-9-]+\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(\\/[^\\s]*)?\$",
        message = "URL must be a valid format"
    )
    val url: String,
)
