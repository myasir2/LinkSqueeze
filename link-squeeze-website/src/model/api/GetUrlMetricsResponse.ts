import {UrlMetric} from "../UrlMetric";

export class GetUrlMetricsResponse {

  metrics: UrlMetric[]

  constructor(metrics: UrlMetric[]) {
    this.metrics = metrics;
  }
}
