package ca.myasir.linksqueeze.dao.impl

import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.entity.ShortenedUrlEntity
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Component
class ShortenedUrlDaoImpl : ShortenedUrlDao {
    override fun add(shortenedUrl: ShortenedUrl) {
        return transaction {
            ShortenedUrlEntity.insert {
                it[urlHash] = shortenedUrl.urlHash.value
                it[url] = shortenedUrl.url
                it[userId] = shortenedUrl.userId?.value
                it[expiryDate] = shortenedUrl.expiryDate?.toLocalDateTime()
            }
        }
    }

    override fun get(urlHash: UrlHash): ShortenedUrl? {
        val record =
            transaction {
                ShortenedUrlEntity.selectAll()
                    .limit(1)
                    .where {
                        ShortenedUrlEntity.urlHash eq urlHash.value
                    }
                    .firstOrNull()
            }?.let(::toShortenedUrl)

        return record?.takeIf { it.expiryDate == null || it.expiryDate.isAfter(ZonedDateTime.now()) }
            ?: record
    }

    override fun getByUser(userId: UserId): List<ShortenedUrl> {
        return transaction {
            ShortenedUrlEntity
                .selectAll()
                .where { ShortenedUrlEntity.userId eq userId.value }
                .map(::toShortenedUrl)
        }
    }

    override fun delete(
        urlHash: UrlHash,
        userId: UserId,
    ) {
        transaction {
            ShortenedUrlEntity.deleteWhere {
                ShortenedUrlEntity.urlHash eq urlHash.value
                ShortenedUrlEntity.userId eq userId.value
            }
        }
    }

    private fun toShortenedUrl(row: ResultRow): ShortenedUrl {
        return ShortenedUrl(
            urlHash = UrlHash(row[ShortenedUrlEntity.urlHash]),
            url = row[ShortenedUrlEntity.url],
            userId = row[ShortenedUrlEntity.userId]?.let(::UserId),
            expiryDate = row[ShortenedUrlEntity.expiryDate]?.atZone(ZoneOffset.UTC),
        )
    }
}
