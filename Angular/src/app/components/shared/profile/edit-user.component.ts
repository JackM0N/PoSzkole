import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
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
  styleUrl: '../../../styles/edit-user.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
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
      username: [this.data.user.username],
      password: ['', [Validators.minLength(6)]],
      confirmPassword: [''],
      firstName: [this.data.user.firstName],
      lastName: [this.data.user.lastName],
      gender: [this.data.user.gender],
      email: [this.data.user.email, [Validators.email]],
      phone: [this.data.user.phone],
      hourlyRate: [this.data.user.hourlyRate],
      level: [this.data.user.level],
      guardianPhone: [this.data.user.guardianPhone],
      guardianEmail: [this.data.user.guardianEmail],
      discountProcentage: [this.data.user.discountProcentage],
      issueInvoice: [this.data.user.issueInvoice]
    });
  }

  onSubmit() {
    const originalUser = this.data.user;
    const updatedUser: WebsiteUser = this.userForm.value;
  
    if (this.areUsersEqual(originalUser, updatedUser)) {
      this.toastr.info('Nie wprowadzono żadnych zmian');
      this.dialogRef.close();
      return;
    }

    console.log(this.areUsersEqual(originalUser,updatedUser))

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

  private areUsersEqual(user1: WebsiteUser, user2: WebsiteUser): boolean {
    return (
      this.areValuesEqual(user1.username, user2.username) &&
      this.areValuesEqual(user1.firstName, user2.firstName) &&
      this.areValuesEqual(user1.lastName, user2.lastName) &&
      this.areValuesEqual(user1.gender, user2.gender) &&
      this.areValuesEqual(user1.email, user2.email) &&
      this.areValuesEqual(user1.phone, user2.phone) &&
      this.areValuesEqual(user1.hourlyRate, user2.hourlyRate) &&
      this.areValuesEqual(user1.level, user2.level) &&
      this.areValuesEqual(user1.guardianPhone, user2.guardianPhone) &&
      this.areValuesEqual(user1.guardianEmail, user2.guardianEmail) &&
      this.areValuesEqual(user1.discountProcentage, user2.discountProcentage) &&
      this.areValuesEqual(user1.issueInvoice, user2.issueInvoice)
    );
  }
  
  private areValuesEqual(value1: any, value2: any): boolean {
    return value1 === value2 || (value1 == null && value2 == null);
  }
}
