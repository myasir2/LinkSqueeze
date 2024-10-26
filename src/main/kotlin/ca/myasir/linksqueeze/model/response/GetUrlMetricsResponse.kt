package ca.myasir.linksqueeze.model.response

import ca.myasir.linksqueeze.model.UrlMetric

data class GetUrlMetricsResponse(
    val metrics: List<UrlMetric>,
)
