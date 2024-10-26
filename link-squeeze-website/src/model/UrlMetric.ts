import {Type} from "class-transformer"

export class UrlMetric {

  urlHash: string
  metricType: string
  count: number

  @Type(() => Date)
  date: Date

  constructor(urlHash: string, metricType: string, count: number, date: Date) {
    this.urlHash = urlHash;
    this.metricType = metricType;
    this.count = count;
    this.date = date;
  }
}
