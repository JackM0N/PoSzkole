import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment.prod';
import { Observable } from 'rxjs';
import { WebsiteUser } from '../models/website-user.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient, private router: Router) {}

  //Login methods
  login(userData: {username: string, password: string}): Observable<{ token: string}>{
    return this.http.post<{token: string}>(environment.apiUrl + '/login', userData);
  }

  register(userData: WebsiteUser): Observable<any> {
    return this.http.post<any>(environment.apiUrl + '/register', userData);
  }

  //Token decoding methods
  getToken(): string | null{
    return localStorage.getItem(environment.tokenKey);
  }

  private getDecodedToken() {
    const token = localStorage.getItem(environment.tokenKey);
    if (token) {
      return this.jwtHelper.decodeToken(token);
    }
    return null;
  }

  hasRole(role: string): boolean {
    const decodedToken = this.getDecodedToken();
    return decodedToken?.roles?.some((r: { roleName: string }) => r.roleName === role) || false;
  }

  hasAnyRoles(roles: string[]): boolean {
    const decodedToken = this.getDecodedToken();
    return roles.some(role => decodedToken?.roles?.some((r: { roleName: string }) => r.roleName === role));
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem(environment.tokenKey);
    return !!token && !this.jwtHelper.isTokenExpired(token);
  }

  logout() {
    localStorage.removeItem(environment.tokenKey);
    this.router.navigate(['/']);
  }
}
