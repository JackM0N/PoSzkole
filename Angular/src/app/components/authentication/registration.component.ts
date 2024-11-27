import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { EducationLevel } from "../../enums/education-level.enum";
import { passwordMatchValidator } from "../../validators/password-match.validator";
import { WebsiteUser } from "../../models/website-user.model";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import { Gender } from "../../enums/gender.enum";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrl: '../../styles/auth.component.css',
})
export class RegistrationComponent {
  registrationForm: FormGroup;
  educationLevels = Object.entries(EducationLevel);
  genders = Object.entries(Gender);

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
  ){
    this.registrationForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      level: [null, Validators.required],
      guardianPhone: [''],
      guardianEmail: ['', Validators.email],
      isCashPayment: [false, [Validators.required]],
      issueInvoice: [false, [Validators.required]]
    }, {validators: passwordMatchValidator });
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      const userData: WebsiteUser = {
        username: this.registrationForm.get('username')?.value,
        email: this.registrationForm.get('email')?.value,
        password: this.registrationForm.get('password')?.value,
        firstName: this.registrationForm.get('firstName')?.value,
        lastName: this.registrationForm.get('lastName')?.value,
        gender: this.registrationForm.get('gender')?.value,
        phone: this.registrationForm.get('phone')?.value,
        level: this.registrationForm.get('level')?.value,
        guardianPhone: this.registrationForm.get('guardianPhone')?.value,
        guardianEmail: this.registrationForm.get('guardianEmail')?.value,
        isCashPayment: this.registrationForm.get('isCashPayment')?.value,
        issueInvoice: this.registrationForm.get('issueInvoice')?.value
      }

      this.authService.register(userData).subscribe({
        next: () => {
          this.toastr.success('Rejestracja się powiodła! Możesz się teraz zalogować')
          this.router.navigate(['/login'])
        },
        error: (error) => {
          this.toastr.error('Wystąpił błąd podczas rejestracji. Spróbuj ponownie później')
          console.error('Error during registration', error);
        }
      });
    };
  }
}