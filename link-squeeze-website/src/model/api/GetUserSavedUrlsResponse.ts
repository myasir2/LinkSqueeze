import {UserSavedUrl} from "../UserSavedUrl";

export class GetUserSavedUrlsResponse {

  urls: UserSavedUrl[];

  constructor(urls: UserSavedUrl[]) {
    this.urls = urls;
  }
}
