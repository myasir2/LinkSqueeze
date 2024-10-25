import {Component, OnInit} from '@angular/core';
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
        AsyncPipe
    ],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
    title = 'link-squeeze-website';
    isFormSubmitting: boolean = false
    form: FormGroup

    private readonly currenDate = new Date();
    readonly minDate = addDays(this.currenDate, 1)
    readonly maxDate = addDays(this.currenDate, 30);

    constructor(
        private api: BackendApiService,
        private formBuilder: FormBuilder,
        public auth: AuthService
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
        const savedUrls = await this.api.getUserSavedUrls()

        console.log(savedUrls)
    }

    async onLogin() {
        this.auth.loginWithRedirect()
    }

    async onLogOut() {
        this.auth.loginWithRedirect()
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
