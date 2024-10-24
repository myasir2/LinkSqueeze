package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.util.Constants.SHORTENED_URL_LENGTH
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.PathVariable

data class RedirectToUrlRequest(

    @PathVariable("hashId", required = true)
    @Min(value = SHORTENED_URL_LENGTH)
    @Max(value = SHORTENED_URL_LENGTH)
    val hashId: String
)
