package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.model.UserSavedUrl

data class GetUserSavedUrlsResponse(

    val urls: List<UserSavedUrl>
)
