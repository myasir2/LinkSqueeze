package ca.myasir.linksqueeze.dao.impl

import ca.myasir.linksqueeze.dao.MetricType
import ca.myasir.linksqueeze.dao.UrlMetricsDao
import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.model.entity.UrlMetricEntity
import ca.myasir.linksqueeze.util.UserId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class UrlMetricsDaoImpl: UrlMetricsDao {

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
}
