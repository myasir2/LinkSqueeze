package ca.myasir.linksqueeze.model.entity

import ca.myasir.linksqueeze.dao.MetricType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.math.BigDecimal
import java.time.LocalDateTime

object UrlMetricEntity: Table("url_metric") {
    val id = integer("id").autoIncrement()
    val urlHash = reference("url_hash", ShortenedUrlEntity.urlHash, onDelete = ReferenceOption.CASCADE)
    val metricType = enumeration("type", MetricType::class)
    val count = decimal(name = "value", 2, 1).default(BigDecimal(0))
    val date = datetime(name = "date").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
