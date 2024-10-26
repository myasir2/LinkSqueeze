package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.util.Constants.URL_HASH_LENGTH
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.web.bind.annotation.PathVariable

data class GetUrlMetricsRequest(

    @PathVariable("hashId", required = true)
    @Min(value = URL_HASH_LENGTH)
    @Max(value = URL_HASH_LENGTH)
    val hashId: String
)
