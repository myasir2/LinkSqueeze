import {Type} from "class-transformer"

export class UrlDetails {

    originalUrl: string
    shortenedUrl: string

  @Type(() => Date)
      expiry?: Date | null = null

  constructor(originalUrl: string, shortenedUrl: string, expiry: Date | null) {
      this.originalUrl = originalUrl
      this.shortenedUrl = shortenedUrl
      this.expiry = expiry
  }
}
