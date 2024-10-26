import {UrlDetails} from "../url-details"

export class CreateShortenedUrlResponse {

    urlDetails: UrlDetails

    constructor(urlDetails: UrlDetails) {
        this.urlDetails = urlDetails
    }
}
