package ca.myasir.linksqueeze.dao.impl

import ca.myasir.linksqueeze.dao.UrlMetricsDao
import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.model.entity.UrlMetricEntity
import ca.myasir.linksqueeze.util.UrlHash
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.ZoneOffset

@Component
class UrlMetricsDaoImpl : UrlMetricsDao {
    override fun addMetric(urlMetric: UrlMetric) {
        transaction {
            UrlMetricEntity.upsert {
                it[urlHash] = urlMetric.urlHash.value
                it[metricType] = urlMetric.metricType
                it[count] = BigDecimal(urlMetric.count)
                it[date] = urlMetric.date.toLocalDateTime()
            }
        }
    }

    override fun getMetrics(urlHash: UrlHash): List<UrlMetric> {
        return transaction {
            UrlMetricEntity.selectAll()
                .where(UrlMetricEntity.urlHash eq urlHash.value)
                .map(::toUrlMetric)
        }
    }

    private fun toUrlMetric(row: ResultRow): UrlMetric {
        return UrlMetric(
            urlHash = UrlHash(row[UrlMetricEntity.urlHash]),
            metricType = row[UrlMetricEntity.metricType],
            count = row[UrlMetricEntity.count].toDouble(),
            date = row[UrlMetricEntity.date].atZone(ZoneOffset.UTC),
        )
    }
}
