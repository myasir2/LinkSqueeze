import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {ClassConstructor, plainToInstance} from "class-transformer";
import {catchError, firstValueFrom, map, throwError} from "rxjs";
import {ApiServiceGenericError} from "../model/error/api-service-generic-error";
import {UserSavedUrl} from "../model/UserSavedUrl";
import {GetUserSavedUrlsResponse} from "../model/api/GetUserSavedUrlsResponse";
import {UrlMetric} from "../model/UrlMetric";
import {GetUrlMetricsResponse} from "../model/api/GetUrlMetricsResponse";

@Injectable({
  providedIn: 'root'
})
export class BackendApiService {

  private readonly commonUpdateHeaders: HttpHeaders = new HttpHeaders({
    'Content-Type': "application/json"
  })

  constructor(
    @Inject('BASE_API_URL') private baseUrl: string,
    private httpClient: HttpClient
  ) {
  }

  public async getUserSavedUrls(): Promise<UserSavedUrl[]> {
    const endpoint = `${this.baseUrl}/user/urls`
    const response = await this.getRequest(endpoint, GetUserSavedUrlsResponse)

    return Promise.resolve(response.urls)
  }

  public async getUrlMetrics(urlHash: string): Promise<UrlMetric[]> {
    const endpoint = `${this.baseUrl}/metrics/${urlHash}`

    const response = await this.getRequest(endpoint, GetUrlMetricsResponse)

    return Promise.resolve(response.metrics)
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

  public async postRequest<T>(endpoint: string, object: T): Promise<void> {
    return firstValueFrom(
      this.httpClient.post<void>(endpoint, object, {headers: this.commonUpdateHeaders}).pipe(
        catchError(this.handleError)
      )
    )
  }

  private handleError(error: HttpErrorResponse) {
    return throwError(() => new ApiServiceGenericError(`Generic error: ${error.status} - ${error.message}`));
  }
}
