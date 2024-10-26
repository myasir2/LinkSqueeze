import {UrlDetails} from "../url-details"

export class GetUserSavedUrlsResponse {

    urls: UrlDetails[]

    constructor(urls: UrlDetails[]) {
        this.urls = urls
    }
}
