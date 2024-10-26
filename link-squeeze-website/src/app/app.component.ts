import {Component, Inject, OnInit} from "@angular/core";
import {RouterOutlet} from "@angular/router";
import {getCommonTextFieldValidator} from "../util/validation-utils";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {ReactiveValidationModule} from "angular-reactive-validation";
import {MatButtonModule} from "@angular/material/button";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {provideNativeDateAdapter} from "@angular/material/core";
import {addDays} from "date-fns";
import {AuthService} from "@auth0/auth0-angular";
import {BackendApiService} from "../service/backend-api.service";
import {UrlDetails} from "../model/url-details";
import {MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatDialog} from "@angular/material/dialog";
import {MetricsComponent, MetricsMetadata} from "./metrics-component/metrics.component";
import {firstValueFrom} from "rxjs";
import {SnackbarService} from "../service/snackbar.service";

@Component({
  selector: "app-root",
  standalone: true,
  providers: [
    provideNativeDateAdapter()
  ],
  imports: [
    RouterOutlet,
    ReactiveFormsModule,
    NgIf,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinner,
    ReactiveValidationModule,
    MatButtonModule,
    MatDatepickerModule,
    AsyncPipe,
    MatTableModule,
    MatIconModule,
    MatMenuModule
  ],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.scss",
})
export class AppComponent implements OnInit {
  private readonly currenDate = new Date();

  readonly minDate = addDays(this.currenDate, 1)
  readonly maxDate = addDays(this.currenDate, 30);

  title = "link-squeeze-website";
  isFormSubmitting = false
  form: FormGroup
  savedUrls: UrlDetails[] = []
  displayedColumns: string[] = ["url", "shortUrl", "expiry", "actions"];
  isAuthenticated = false;

  constructor(
    private api: BackendApiService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private snackBarService: SnackbarService,
    public auth: AuthService,
    @Inject("BASE_API_URL") public baseApiUrl: string
  ) {
    this.form = this.formBuilder.group({
      url: [
        "",
        [
          ...getCommonTextFieldValidator("URL", 5, 100),
          Validators.pattern("^(https?:\\/\\/)(www\\.)?([a-zA-Z0-9-]+\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(\\/[^\\s]*)?$")
        ]
      ],
      expiryDate: [null],
    })
  }

  async ngOnInit() {
    this.auth.isAuthenticated$.subscribe(isAuthenticated => {
      console.log(`Subscription response: isAuthenticated: ${isAuthenticated}`);

      this.isAuthenticated = isAuthenticated;

      if (isAuthenticated) {
        this.api.getUserSavedUrls()
          .then(results => this.savedUrls = results)
          .catch(error => {
            console.error(error)

            this.snackBarService.showAlertSnackbar(`Error from backend: ${error}`)
          })
      }
    })
  }

  async onLogin() {
    this.auth.loginWithRedirect()
  }

  async onLogOut() {
    this.auth.logout()
  }

  async onViewMetricsClick(shortenedUrl: string) {
    // Get URL path. The first char in the index is always a "/"
    const url = new URL(shortenedUrl)
    const urlHash = url.pathname.slice(1)

    this.dialog.open<MetricsComponent, MetricsMetadata, unknown>(MetricsComponent, {
      data: {
        urlHash,
      },
    })
  }

  async onDeleteUrl(shortenedUrl: string) {
    // Get URL path. The first char in the index is always a "/"
    const url = new URL(shortenedUrl)
    const urlHash = url.pathname.slice(1)

    try {
      await this.api.deleteUrl(urlHash)

      this.savedUrls = this.savedUrls.filter(s => s.shortenedUrl !== shortenedUrl)
    }
    catch (error) {
      console.error(error)

      this.snackBarService.showAlertSnackbar(`Error from backend: ${error}`)
    }
  }

  async onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched()

      return;
    }

    this.isFormSubmitting = true
    this.form.disable()

    const formValues = this.form.value
    const url = formValues.url
    const expiryDate: Date | null = formValues?.expiryDate
    const isAuthenticated = await firstValueFrom(this.auth.isAuthenticated$)

    try {
      const savedUrl = await this.api.createShortenedUrl(url, expiryDate, isAuthenticated)

      this.savedUrls = [
        ...this.savedUrls,
        savedUrl
      ]

      this.snackBarService.showSuccessSnackbar(`Shortened URL Generated: ${savedUrl.shortenedUrl}`)
    }
    catch (error) {
      console.error(error)

      this.snackBarService.showAlertSnackbar(`Error from backend: ${error}`)
    }
    finally {
      this.isFormSubmitting = false
      this.form.enable()
    }
  }
}
