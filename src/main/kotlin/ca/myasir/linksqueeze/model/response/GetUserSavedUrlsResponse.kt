package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.model.UrlDetails

data class GetUserSavedUrlsResponse(
    val urls: List<UrlDetails>,
)
