package ca.myasir.linksqueeze.model.entity

import ca.myasir.linksqueeze.util.Constants.URL_HASH_LENGTH
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ShortenedUrlEntity : Table("shortened_urls") {
    val urlHash = varchar("url_hash", URL_HASH_LENGTH.toInt())
    val url = varchar("url", 255)
    val userId = varchar("user_id", 255).nullable()
    val expiryDate = datetime("expiry_date").nullable()

    override val primaryKey = PrimaryKey(urlHash)
}
