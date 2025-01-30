import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    // Pobierz wymagane role z `data` w routing module
    const requiredRoles = route.data['roles'] as string[];

    if (!this.authService.isLoggedIn()) {
      // Użytkownik nie jest zalogowany -> przekierowanie do loginu
      this.router.navigate(['/login']);
      return false;
    }

    if (requiredRoles && !this.authService.hasAnyRoles(requiredRoles)) {
      // Użytkownik nie ma wymaganych ról -> przekierowanie na stronę access denied
      this.router.navigate(['/access-denied']);
      return false;
    }

    // Dostęp jest dozwolony
    return true;
  }
}