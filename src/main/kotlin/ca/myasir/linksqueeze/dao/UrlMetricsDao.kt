package ca.myasir.linksqueeze.dao

import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.util.UrlHash

/**
 * This interface will be responsible to interact with the url metrics database/service
 */
interface UrlMetricsDao {
    /**
     * This method will add a new metric row for the given user/user id combo, and a count of +1
     */
    fun addMetric(urlMetric: UrlMetric)

    /**
     * This method will return metrics for the given URL hash
     */
    fun getMetrics(urlHash: UrlHash): List<UrlMetric>
}

enum class MetricType {
    COUNT,
}
