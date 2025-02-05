import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { WebsiteUser } from '../../../models/website-user.model';
import { MatDialogRef } from '@angular/material/dialog';
import { Gender } from '../../../enums/gender.enum';
import { JwtHelperService } from '@auth0/angular-jwt';
import { passwordMatchValidator } from '../../../validators/password-match.validator';

function getEnumKeyByValue<T extends object>(enumObject: T, value: string): string | undefined {
  return Object.keys(enumObject).find(key => enumObject[key as keyof T] === value);
}

@Component({
  selector: 'app-register-teacher',
  templateUrl: './register-teacher.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class RegisterTeacherComponent {
  registerTeacherForm: FormGroup;
  genders: Gender[] = [];
  jwtHelper = new JwtHelperService();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService,
    public dialogRef: MatDialogRef<RegisterTeacherComponent>
  ) {
    this.genders = Object.values(Gender);
    this.registerTeacherForm = this.fb.group({
      username: [null, [Validators.required, Validators.minLength(3)]],
      password: [null, [Validators.required, Validators.minLength(6)]],
      confirmPassword: [null, [Validators.required, Validators.minLength(6)]],
      firstName: [null, [Validators.required]],
      lastName: [null, [Validators.required]],
      gender: [null, [Validators.required]],
      email: [null, [Validators.required, Validators.email]],
      phone: [null, [Validators.required]],
      hourlyRate: [null, [Validators.required, Validators.min(0)]]
    }, {validators: passwordMatchValidator });
  }

  onSubmit() {
    if (this.registerTeacherForm.invalid) {
      this.toastr.error('Formularz zawiera błędy. Popraw dane i spróbuj ponownie.', 'Błąd');
      return;
    }

    const teacherData: WebsiteUser = {
      ...this.registerTeacherForm.value,
      gender: getEnumKeyByValue(Gender, this.registerTeacherForm.value.gender) as Gender,
    };

    this.authService.registerTeacher(teacherData).subscribe({
      next: (response) => {
        const decodedToken = this.jwtHelper.decodeToken(response.token);
        const userId = decodedToken.id;

        this.toastr.success('Rejestracja zakończona sukcesem.', 'Sukces');
        this.dialogRef.close(userId);
      },
      error: (error) => {
        if (error.error?.includes('website_user_username_key')) {
          this.registerTeacherForm.get('username')?.setErrors({ usernameTaken: true });
        }else if (error.error?.includes('website_user_email_key')) {
          this.registerTeacherForm.get('email')?.setErrors({ emailTaken: true });
        }else if (error.error?.includes('website_user_phone_key')) {
          this.registerTeacherForm.get('phone')?.setErrors({ phoneTaken: true });
        }

        this.toastr.error('Wystąpił błąd podczas rejestracji.', 'Błąd');
        console.error(error);
      }
    });
    
  }

  onCancel(): void {
    this.dialogRef.close(true);
  }
}
