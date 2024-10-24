package ca.myasir.linksqueeze.dao.impl

import ca.myasir.linksqueeze.dao.ShortenedUrlDao
import ca.myasir.linksqueeze.model.ShortenedUrl
import ca.myasir.linksqueeze.model.entity.ShortenedUrlEntity
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import org.apache.catalina.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneOffset

class ShortenedUrlDaoImpl: ShortenedUrlDao {

    override fun add(shortenedUrl: ShortenedUrl) {
        return transaction {
            StdOutSqlLogger

            ShortenedUrlEntity.insert {
                it[urlHash] = shortenedUrl.urlHash.value
                it[url] = shortenedUrl.url
                it[userId] = shortenedUrl.userId?.value
                it[expiryDate] = shortenedUrl.expiryDate?.toLocalDateTime()
            }
        }
    }

    override fun getByUser(userId: UserId): List<ShortenedUrl> {
        return transaction {
            ShortenedUrlEntity
                .selectAll()
                .where { ShortenedUrlEntity.userId eq userId.value }
                .map(::toShortenedUrl)
        }
    }

    override fun delete(urlHash: UrlHash) {
        transaction {
            ShortenedUrlEntity.deleteWhere {
                ShortenedUrlEntity.urlHash eq urlHash.value
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
