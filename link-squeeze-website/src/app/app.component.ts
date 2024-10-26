import {Component, Inject, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {getCommonTextFieldValidator} from "../util/validation-utils";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {ReactiveValidationModule} from 'angular-reactive-validation';
import {MatButtonModule} from '@angular/material/button';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {provideNativeDateAdapter} from '@angular/material/core';
import {addDays} from 'date-fns';
import {AuthService} from '@auth0/auth0-angular';
import {BackendApiService} from "../service/backend-api.service";
import {UserSavedUrl} from "../model/UserSavedUrl";
import {MatTableModule} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatDialog} from '@angular/material/dialog';
import {MetricsComponent, MetricsMetadata} from "./metrics-component/metrics.component";

@Component({
    selector: 'app-root',
    standalone: true,
    providers: [
        provideNativeDateAdapter(),
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
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
    private readonly currenDate = new Date();

    readonly minDate = addDays(this.currenDate, 1)
    readonly maxDate = addDays(this.currenDate, 30);

    title = 'link-squeeze-website';
    isFormSubmitting: boolean = false
    form: FormGroup
    savedUrls: UserSavedUrl[] = []
    displayedColumns: string[] = ["url", "shortUrl", "expiry", "actions"];

    constructor(
        private api: BackendApiService,
        private formBuilder: FormBuilder,
        private dialog: MatDialog,
        public auth: AuthService,
        @Inject('BASE_API_URL') public baseApiUrl: string,
    ) {
        this.form = this.formBuilder.group({
            url: [
                "",
                [
                    ...getCommonTextFieldValidator("URL", 5, 100),
                    Validators.pattern("^(https?:\\/\\/)?(www\\.)?([a-zA-Z0-9-]+\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(\\/[^\\s]*)?\$")
                ]
            ],
            expiryDate: [new Date(), Validators.required],
        })
    }

    async ngOnInit() {
        this.savedUrls = await this.api.getUserSavedUrls()
    }

    async onLogin() {
        this.auth.loginWithRedirect()
    }

    async onLogOut() {
        this.auth.loginWithRedirect()
    }

    async onViewMetricsClick(urlHash: string) {
        this.dialog.open<MetricsComponent, MetricsMetadata, any>(MetricsComponent, {
            data: {
                urlHash
            }
        })
    }

    async onDeleteUrl(url: string) {
        console.log("Delete url for URL: " + url)
    }

    async onSubmit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched()

            return;
        }

        const formValues = this.form.value

        console.log(formValues)
    }
}
