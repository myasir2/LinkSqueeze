import {Injectable} from "@angular/core"
import {MatSnackBar} from "@angular/material/snack-bar"

@Injectable({
    providedIn: "root",
})
export class SnackbarService {

    constructor(private snackbar: MatSnackBar) {
    }

    showAlertSnackbar(message: string) {
        this.snackbar.open(message, "Close", {
            duration: 10000,
            panelClass: ["snackbar-alert"],
        })
    }

    showSuccessSnackbar(message: string) {
        this.snackbar.open(message, "Close", {
            panelClass: ["snackbar-success"],
        })
    }
}
