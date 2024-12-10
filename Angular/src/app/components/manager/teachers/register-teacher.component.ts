import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { WebsiteUser } from '../../../models/website-user.model';
import { MatDialogRef } from '@angular/material/dialog';
import { Gender } from '../../../enums/gender.enum';

@Component({
  selector: 'app-register-teacher',
  templateUrl: './register-teacher.component.html',
  styleUrl: '../../../styles/request-form.component.css'
})
export class RegisterTeacherComponent {
  registerTeacherForm: FormGroup;
  genders: Gender[] = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService,
    public dialogRef: MatDialogRef<RegisterTeacherComponent>
  ) {
    this.genders = Object.values(Gender);
    this.registerTeacherForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      hourlyRate: ['', [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit() {
    if (this.registerTeacherForm.invalid) {
      this.toastr.error('Formularz zawiera błędy. Popraw dane i spróbuj ponownie.', 'Błąd');
      return;
    }

    const teacherData: WebsiteUser = {
      ...this.registerTeacherForm.value,
      gender: Object.keys(Gender).find(
        key => Gender[key as keyof typeof Gender]
      )
    };

    this.authService.registerTeacher(teacherData).subscribe({
      next: (response) => {
        this.toastr.success('Rejestracja zakończona sukcesem.', 'Sukces');
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.toastr.error('Wystąpił błąd podczas rejestracji.', 'Błąd');
        console.error(err);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
