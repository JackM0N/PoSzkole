import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EducationLevel } from '../../../enums/education-level.enum';
import { Role } from '../../../models/role.model';
import { Roles } from '../../../enums/role.enum';
import { Gender } from '../../../enums/gender.enum';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { WebsiteUser } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrl: '../../../styles/edit-user.component.css'
})
export class EditUserComponent {
  userForm!: FormGroup;
  genders = Object.values(Gender);
  levels = Object.values(EducationLevel);
  roles = Object.values(Roles);
  jwtHelper = new JwtHelperService();
  currenUserRoles: Role[] = [];
  currentUserIsManager = false;
  isTeacher = this.data.user.roles?.some(role => role.roleName === "TEACHER");
  isStudent = this.data.user.roles?.some(role => role.roleName === "STUDENT");

  constructor(
    private fb: FormBuilder,
    private toastr: ToastrService,
    private dialogRef: MatDialogRef<EditUserComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { user: WebsiteUser },
    private websiteUserService: WebsiteUserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserIsManager = this.checkIfCurrentUserIsManager();

    this.userForm = this.fb.group({
      username: [this.data.user.username, Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      firstName: [this.data.user.firstName, Validators.required],
      lastName: [this.data.user.lastName, Validators.required],
      gender: [this.data.user.gender, Validators.required],
      email: [this.data.user.email, [Validators.required, Validators.email]],
      phone: [this.data.user.phone, Validators.required],
      hourlyRate: [this.data.user.hourlyRate],
      level: [this.data.user.level],
      guardianPhone: [this.data.user.guardianPhone],
      guardianEmail: [this.data.user.guardianEmail],
      discountProcentage: [this.data.user.discountProcentage],
      issueInvoice: [this.data.user.issueInvoice]
    });
  }

  onSubmit() {
    if (this.userForm.invalid) {
      this.toastr.error('Formularz zawiera błędy');
      return;
    }

    if (
      this.userForm.value.password !== this.userForm.value.confirmPassword
    ) {
      this.toastr.error('Hasła muszą być takie same');
      return;
    }

    const updatedUser: WebsiteUser = this.userForm.value;

    this.websiteUserService.editUserProfile(updatedUser).subscribe({
      next: response => {
        this.toastr.success('Dane użytkownika zostały zapisane');
        this.dialogRef.close(response);
      },
      error: error => {
        this.toastr.success('Coś poszło nie tak podczas edycji profilu');
        console.error("Błąd edycji profilu użytkownika", error)
      }
    })
  }

  onCancel() {
    this.dialogRef.close();
  }
  
  private checkIfCurrentUserIsManager(): boolean {
    const token = this.authService.getToken();
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      const userRoles: Role[] = decodedToken.roles || [];
  
      return userRoles.some((role: Role) => role.roleName === "MANAGER");
    }
    return false;
  }
}
