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

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}

@Component({
  selector: 'app-register-student',
  templateUrl: './register-student.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class RegisterStudentComponent {
  registerStudentForm: FormGroup;
  educationLevels = Object.values(EducationLevel);
  genders = Object.values(Gender);
  jwtHelper = new JwtHelperService();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService,
    public dialogRef: MatDialogRef<RegisterStudentComponent>
  ){
    this.registerStudentForm = this.formBuilder.group({
      username: [null, [Validators.required, Validators.minLength(3)]],
      password: [null, [Validators.required, Validators.minLength(6)]],
      confirmPassword: [null, [Validators.required, Validators.minLength(6)]],
      email: [null, [Validators.required, Validators.email]],
      firstName: [null, [Validators.required]],
      lastName: [null, [Validators.required]],
      gender: [null, [Validators.required]],
      phone: [null, [Validators.required]],
      level: [null, Validators.required],
      guardianPhone: [null],
      guardianEmail: [null, Validators.email],
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
      gender: getEnumKeyByValue(Gender, this.registerStudentForm.value.gender) as Gender,
      level: getEnumKeyByValue(EducationLevel, this.registerStudentForm.value.level) as EducationLevel,
    };

    this.authService.registerStudent(studentData).subscribe({
      next: (response) => {
        const decodedToken = this.jwtHelper.decodeToken(response.token);
        const userId = decodedToken.id;

        this.toastr.success('Rejestracja zakończona sukcesem.', 'Sukces');
        this.dialogRef.close(userId);
      },
      error: (error) => {
        this.toastr.error('Wystąpił błąd podczas rejestracji. Sprawdź czy wszystkie dane są poprawne', 'Błąd');
        console.error('Student registration error',error);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
