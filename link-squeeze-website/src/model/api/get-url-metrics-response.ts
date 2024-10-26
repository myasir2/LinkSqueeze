import {UrlMetrics} from "../url-metrics";

export class GetUrlMetricsResponse {

  metrics: UrlMetrics[]

  constructor(metrics: UrlMetrics[]) {
    this.metrics = metrics;
  }
}
