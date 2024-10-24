package ca.myasir.linksqueeze.dao

import ca.myasir.linksqueeze.model.UrlMetric
import ca.myasir.linksqueeze.util.UserId

/**
 * This interface will be responsible to interact with the url metrics database/service
 */
interface UrlMetricsDao {

    /**
     * This will add a new metric row for the given user/user id combo, and a count of +1
     */
    fun addMetric(urlMetric: UrlMetric)
}

enum class MetricType {
    COUNT
}
