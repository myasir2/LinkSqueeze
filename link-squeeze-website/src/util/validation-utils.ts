import {Validators} from "angular-reactive-validation";
import {ValidatorFn} from "@angular/forms";

export const getCommonTextFieldValidator = (label: string, min: number, max: number): ValidatorFn[] => {
    return [
        Validators.required(`${label} is required`),
        Validators.minLength(min, minLength => `${label} must be greater than ${minLength} characters`),
        Validators.maxLength(max, maxLength => `${label} must be less than ${maxLength} characters`)
    ]
}
