import { Component, EventEmitter, Input, Output } from '@angular/core';
import { WebsiteUser } from '../../../models/website-user.model';
import { EducationLevel } from '../../../enums/education-level.enum';
import { Role } from '../../../enums/role.enum';
import { MatDialog } from '@angular/material/dialog';
import { EditUserComponent } from './edit-user.component';
import { EditSubjectsComponent } from './edit-subjects.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: '../../../styles/profile.component.css'
})
export class AccountComponent {
  @Input() account?: WebsiteUser;
  @Input() isOwner: boolean = false;
  @Output() refreshAccountRequested = new EventEmitter<void>();

  constructor(private dialog: MatDialog){}

  getRole(role: string | undefined): string {
    if (!role) {
      return 'Brak danych';
    }
    return Role[role as keyof typeof Role]
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

  openEditUser(): void {
    const dialogRef = this.dialog.open(EditUserComponent, {
      width: '50%',
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
      width: '50%',
      data: { subjects: this.account?.subjects, userId: this.account?.id }
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
