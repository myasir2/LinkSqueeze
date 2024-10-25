import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {ClassConstructor, plainToInstance} from "class-transformer";
import {catchError, firstValueFrom, map, throwError} from "rxjs";
import {ApiServiceGenericError} from "../model/error/api-service-generic-error";
import {UserSavedUrl} from "../model/UserSavedUrl";
import {GetUserSavedUrlsResponse} from "../model/GetUserSavedUrlsResponse";

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

    console.log(endpoint)

    const response = await firstValueFrom(
      this.httpClient.get<GetUserSavedUrlsResponse>(endpoint).pipe(
        map(result => plainToInstance(GetUserSavedUrlsResponse, result))
      )
    )

    return Promise.resolve(response.urls)
  }

  public getRequest<T>(endpoint: string, clazz: ClassConstructor<T>): Promise<T> {
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
