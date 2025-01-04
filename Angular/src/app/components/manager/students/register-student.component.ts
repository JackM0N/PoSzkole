import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EducationLevel } from '../../../enums/education-level.enum';
import { Gender } from '../../../enums/gender.enum';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { passwordMatchValidator } from "../../../validators/password-match.validator";
import { MatDialogRef } from '@angular/material/dialog';
import { WebsiteUser } from '../../../models/website-user.model';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-register-student',
  templateUrl: './register-student.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class RegisterStudentComponent {
  registerStudentForm: FormGroup;
  educationLevels = Object.entries(EducationLevel);
  genders = Object.entries(Gender);
  jwtHelper = new JwtHelperService();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService,
    public dialogRef: MatDialogRef<RegisterStudentComponent>
  ){
    this.registerStudentForm = this.formBuilder.group({
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
    if (this.registerStudentForm.invalid) {
      this.toastr.error('Formularz zawiera błędy. Popraw dane i spróbuj ponownie.', 'Błąd');
      return;
    }

    const studentData: WebsiteUser = {
      ...this.registerStudentForm.value,
      gender: Object.keys(Gender).find(
        key => Gender[key as keyof typeof Gender]
      ),
      level: Object.keys(EducationLevel).find(
        key => EducationLevel[key as keyof typeof EducationLevel]
      )
    };

    this.authService.registerStudent(studentData).subscribe({
      next: (response) => {
        const decodedToken = this.jwtHelper.decodeToken(response.token);
        const userId = decodedToken.id;

        this.toastr.success('Rejestracja zakończona sukcesem.', 'Sukces');
        this.dialogRef.close(userId);
      },
      error: (error) => {
        this.toastr.error('Wystąpił błąd podczas rejestracji.', 'Błąd');
        console.error('Student registration error',error);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
