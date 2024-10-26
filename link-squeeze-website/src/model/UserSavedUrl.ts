import {Type} from "class-transformer";

export class UserSavedUrl {

  url: string
  urlHash: string

  @Type(() => Date)
  expiry?: Date

  constructor(expiry: Date, url: string, urlHash: string) {
    this.expiry = expiry;
    this.url = url;
    this.urlHash = urlHash;
  }
}
