package ca.myasir.linksqueeze.model

import ca.myasir.linksqueeze.dao.MetricType
import ca.myasir.linksqueeze.util.UrlHash
import ca.myasir.linksqueeze.util.UserId
import java.time.ZonedDateTime

data class UrlMetric(
    val urlHash: UrlHash,
    val metricType: MetricType,
    val count: Double,
    val date: ZonedDateTime
)
