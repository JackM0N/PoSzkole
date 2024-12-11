import { Component } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import { environment } from "../../../environment/environment";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: '../../styles/auth.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  
  constructor(
     private authService: AuthService,
     private router: Router,
     private formBuilder: FormBuilder,
     private toastr: ToastrService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void{
    localStorage.clear();

    const userData = this.loginForm.value;

    this.authService.login(userData).subscribe({
      next: response => {
        localStorage.setItem(environment.tokenKey, response.token);
        this.toastr.success("Logowanie się powiodło! Witaj z powrotem", "Sukces!")
        this.router.navigate(['/'])
      },
      error: error => {
        this.toastr.error("Coś poszło nie tak. Sprawdź swój login lub hasło", "O nie!")
        console.error('Login error', error);
      }
    })
  }
}