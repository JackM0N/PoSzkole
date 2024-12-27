import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Angular';
  currentUserIsStudent: boolean = false;
  currentUserIsTeacher: boolean = false;
  currentUserIsManager: boolean = false;

  constructor(
    public authService: AuthService,
  ) {
    this.currentUserIsStudent = this.authService.hasRole('STUDENT');
    this.currentUserIsTeacher = this.authService.hasRole('TEACHER');
    this.currentUserIsManager = this.authService.hasRole('MANAGER');
  }

  reloadRoles(){
    this.currentUserIsStudent = this.authService.hasRole('STUDENT');
    this.currentUserIsTeacher = this.authService.hasRole('TEACHER');
    this.currentUserIsManager = this.authService.hasRole('MANAGER');
  }
}
