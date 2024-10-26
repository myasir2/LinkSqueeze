import {Inject, Injectable} from "@angular/core"
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http"
import {ClassConstructor, plainToInstance} from "class-transformer"
import {catchError, firstValueFrom, map, throwError} from "rxjs"
import {ApiServiceGenericError} from "../model/error/api-service-generic-error"
import {UrlDetails} from "../model/url-details"
import {GetUserSavedUrlsResponse} from "../model/api/get-user-saved-urls-response"
import {UrlMetrics} from "../model/url-metrics"
import {GetUrlMetricsResponse} from "../model/api/get-url-metrics-response"
import {CreateShortenedUrlResponse} from "../model/api/create-shortened-url-response"

@Injectable({
    providedIn: "root",
})
export class BackendApiService {

    private readonly commonUpdateHeaders: HttpHeaders = new HttpHeaders({
        "Content-Type": "application/json",
    })

    constructor(
    @Inject("BASE_API_URL") private baseUrl: string,
    private httpClient: HttpClient
    ) {
    }

    public async getUserSavedUrls(): Promise<UrlDetails[]> {
        const endpoint = `${this.baseUrl}/user/url`
        const response = await this.getRequest(endpoint, GetUserSavedUrlsResponse)

        return Promise.resolve(response.urls)
    }

    public async getUrlMetrics(urlHash: string): Promise<UrlMetrics[]> {
        const endpoint = `${this.baseUrl}/user/url/metrics/${urlHash}`
        const response = await this.getRequest(endpoint, GetUrlMetricsResponse)

        return Promise.resolve(response.metrics)
    }

    public async createShortenedUrl(url: string, expiry: Date | null, isAuthenticated: boolean): Promise<UrlDetails> {
        let endpoint = `${this.baseUrl}/generate`

        if (isAuthenticated) {
            endpoint = `${this.baseUrl}/user/url/generate`
        }

        const request = {
            "url": url,
            "expiry": expiry?.toISOString(),
        }
        const response = await this.postRequest(endpoint, request, CreateShortenedUrlResponse)

        return Promise.resolve(response.urlDetails)
    }

    public async deleteUrl(urlHash: string): Promise<void> {
        const endpoint = `${this.baseUrl}/user/url/${urlHash}`

        return firstValueFrom(
            this.httpClient.delete<void>(endpoint, {headers: this.commonUpdateHeaders,}).pipe(
                catchError(this.handleError)
            )
        )
    }

    private getRequest<T>(endpoint: string, clazz: ClassConstructor<T>): Promise<T> {
        return firstValueFrom(
            this.httpClient.get<T>(endpoint).pipe(
                map(result => {
                    return plainToInstance(clazz, result)
                })
            )
        )
    }

    private async postRequest<T, R>(endpoint: string, object: T, clazz: ClassConstructor<R>): Promise<R> {
        return firstValueFrom(
            this.httpClient.post<void>(endpoint, object, {headers: this.commonUpdateHeaders,}).pipe(
                catchError(this.handleError),
                map(result => {
                    return plainToInstance(clazz, result)
                })
            )
        )
    }

    private handleError(error: HttpErrorResponse) {
        return throwError(() => new ApiServiceGenericError(`Generic error: ${error.status} - ${error.message}`))
    }
}
