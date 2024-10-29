import { Component } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import { environment } from "../../../environment/environment";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: '../../styles/auth.component.css'
})
export class LoginComponent {
  loginData = {
    username: '',
    password: ''
  };
  errorMessage: string | null = null;
  
  constructor(
     private authService: AuthService,
     private router: Router
  ) {}

  onSubmit(): void{
    localStorage.clear();

    this.authService.login(this.loginData).subscribe({
      next: response => {
        localStorage.setItem(environment.tokenKey, response.token);
        this.router.navigate(['/'])
      },
      error: error => {
        console.error('Login error', error);
      }
    })
  }
}