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
import { WebsiteUserService } from '../../../services/website-user.service';
import { ToastrService } from 'ngx-toastr';
import { AppComponent } from '../../../app.component';
import { PopUpDialogComponent } from '../pop-up/pop-up-dialog.component';

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
  isManager: boolean = false;
  isSiteOwner: boolean = false;
  showRoleForm: boolean = false;
  allRoles: { roleName: string, selected: boolean }[] = [];

  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private websiteUserService: WebsiteUserService,
    private toastr: ToastrService,
    private appComponent: AppComponent,
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
      this.isManager = this.hasRole('MANAGER');
      this.isSiteOwner = this.hasRole('OWNER');
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

  confirmDeleteUser(): void {
      const dialogRef = this.dialog.open(PopUpDialogComponent, {
        width: '400px',
        data: {
          title: 'Potwierdzenie usunięcia',
          content: 'Czy na pewno chcesz usunąć to konto?',
          submitText: 'Usuń',
          cancelText: 'Anuluj'
        }
      });
    
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.deleteUser();
        }
      });
    }

  deleteUser() {
    this.websiteUserService.deleteUser(this.account?.id!).subscribe({
      next: () => {
        this.toastr.success('Konto zostało usunięte!', 'Sukces!')
        if(this.isOwner){
          this.authService.logout();
          this.appComponent.reloadRoles();
        }else{
          this.refreshAccount();
        }
      },
      error: error => {
        if (error.error === "This user is a part of an active tutoring class") {
          this.toastr.error('Nie można usunąć konta, ponieważ uczeń chodzi na aktywne zajęcia', 'Błąd!');
        }
        else if (error.error === "This user is a part of an active course") {
          this.toastr.error('Nie można usunąć konta, ponieważ uczeń chodzi na aktywny kurs', 'Błąd!');
        }
        else if (error.error === "This user is a teacher of an active tutoring class") {
          this.toastr.error('Nie można usunąć konta, ponieważ nauczyciel ma niezakończone zajęcia', 'Błąd!');
        }
        else{
          this.toastr.error('Coś poszło nie tak podczas usuwania konta!', 'Błąd!');
          console.error('User delete error',error)
        }
      }
    })
  }

  restoreUser() {
    this.websiteUserService.restoreUser(this.account?.id!).subscribe({
      next: () => {
        this.refreshAccount();
        this.toastr.success('Konto zostało przywrócone!', 'Sukces!');
      },
      error: (error) => {
        this.toastr.error('Coś poszło nie tak podczas przywracania konta!', 'Błąd!');
        console.error('User restore error',error);
      }
    })
  }

  refreshAccount(): void {
    this.refreshAccountRequested.emit();
  }

  toggleRoleForm(): void {
    this.showRoleForm = !this.showRoleForm;
  
    if (this.showRoleForm && this.account?.roles) {
      this.allRoles = Object.keys(Roles)
      .filter(roleName => roleName !== 'OWNER')
      .map(roleName => ({
        roleName,
        selected: this.hasRole(roleName)
      }));
    }
  }

  updateRoles(): void {
    const updatedRoles = this.allRoles
      .filter(role => role.selected)
      .map(role => ({ roleName: role.roleName }));
  
    if (this.account) {
      this.authService.changeRoles(this.account.id!, updatedRoles).subscribe({
        next: () => {
          this.toastr.success('Role użytkownika zostały zaktualizowane!', 'Sukces!');
          this.refreshAccount();
          this.toggleRoleForm();
        },
        error: (error) => {
          this.toastr.error('Nie udało się zaktualizować ról użytkownika.', 'Błąd!');
          console.error('User role update error', error);
        }
      });
    }
  }
}
