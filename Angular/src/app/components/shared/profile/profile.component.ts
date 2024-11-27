import { Component, Input } from '@angular/core';
import { WebsiteUser } from '../../../models/website-user.model';
import { EducationLevel } from '../../../enums/education-level.enum';
import { Role } from '../../../enums/role.enum';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: '../../../styles/account.component.css'
})
export class AccountComponent {
  @Input() account?: WebsiteUser;

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
}
