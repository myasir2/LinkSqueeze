import {Type} from "class-transformer";

export class UserSavedUrl {

  url: string
  shortenedUrl: string

  @Type(() => Date)
  expiryDate?: Date

  constructor(expiryDate: Date, url: string, shortenedUrl: string) {
    this.expiryDate = expiryDate;
    this.url = url;
    this.shortenedUrl = shortenedUrl;
  }
}
