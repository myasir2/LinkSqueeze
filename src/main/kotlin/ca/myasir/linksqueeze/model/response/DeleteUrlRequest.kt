package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.util.Constants.MAX_HASH_LENGTH
import ca.myasir.linksqueeze.util.Constants.SHORTENED_URL_LENGTH
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

data class DeleteUrlRequest(

    @PathVariable("hashId", required = true)
    @Min(value = SHORTENED_URL_LENGTH)
    @Max(value = SHORTENED_URL_LENGTH)
    val hashId: String
)
