import {ApplicationConfig, provideZoneChangeDetection} from "@angular/core";
import {provideRouter} from "@angular/router";

import {routes} from "./app.routes";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {environment} from "../environments/environment";
import {AuthHttpInterceptor, provideAuth0} from "@auth0/auth0-angular";
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true,}),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptorsFromDi()),
    provideAuth0({
      domain: environment.auth0.domain,
      clientId: environment.auth0.clientId,
      useRefreshTokens: true,
      authorizationParams: {
        redirect_uri: window.location.origin,
        audience: "https://linksqueeze.myasir.ca",
      },
      httpInterceptor: {
        allowedList: [
          {
            // Replace with your API URL (or use a pattern)
            uri: `${environment.backendApiUrl}/user/*`,
          }
        ],
      },
    }),
    {
      provide: HTTP_INTERCEPTORS, useClass: AuthHttpInterceptor, multi: true,
    },
    {
      provide: "BASE_API_URL",
      useValue: environment.backendApiUrl,
    }
  ],
};
