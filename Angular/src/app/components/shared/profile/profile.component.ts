import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { WebsiteUser } from '../../../models/website-user.model';
import { EducationLevel } from '../../../enums/education-level.enum';
import { Roles } from '../../../enums/role.enum';
import { MatDialog } from '@angular/material/dialog';
import { EditUserComponent } from './edit-user.component';
import { EditSubjectsComponent } from './edit-subjects.component';
import { AuthService } from '../../../services/auth.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Role } from '../../../models/role.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: '../../../styles/profile.component.css'
})
export class AccountComponent implements OnInit, OnChanges{
  @Input() account?: WebsiteUser;
  @Input() isOwner: boolean = false;
  @Output() refreshAccountRequested = new EventEmitter<void>();

  jwtHelper = new JwtHelperService();
  currentUserIsManager: boolean = false;
  isStudent: boolean = false;
  isTeacher: boolean = false;

  constructor(
    private dialog: MatDialog,
    private authService: AuthService
  ){}

  ngOnInit(): void {
    this.currentUserIsManager = this.checkIfCurrentUserIsManager();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['account'] && this.account) {
      this.initializeRoles();
    }
  }

  private initializeRoles(): void {
    if (this.account?.roles) {
      this.isTeacher = this.hasRole('TEACHER');
      this.isStudent = this.hasRole('STUDENT');
    }
  }

  getRole(role: string | undefined): string {
    if (!role) {
      return 'Brak danych';
    }
    return Roles[role as keyof typeof Roles]
  }

  getLevel(level: string | undefined): string {
    if (!level) {
      return 'Brak danych';
    }
    return EducationLevel[level as keyof typeof EducationLevel]
  }

  hasRole(roleName: string): boolean {
    if (!this.account?.roles) {
      return false;
    }
    return this.account.roles.some(role => role.roleName === roleName);
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

  openEditUser(): void {
    const dialogRef = this.dialog.open(EditUserComponent, {
      data: { user: this.account }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.refreshAccount();
      }
    });
  }

  openEditSubjects() {
    const dialogRef = this.dialog.open(EditSubjectsComponent, {
      width: '30%',
      data: { subjects: this.account?.subjects, teacherId: this.account?.id }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.refreshAccount();
      }
    });
  }

  refreshAccount(): void {
    this.refreshAccountRequested.emit();
  }
}
