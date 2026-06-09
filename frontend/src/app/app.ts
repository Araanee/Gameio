import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService, Role } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(private authService: AuthService, private router: Router) {}

  isLoggedIn(): boolean { return this.authService.isLoggedIn(); }
  hasRole(role: Role): boolean { return this.authService.hasRole(role); }
  getUsername(): string { return this.authService.getUser()?.username ?? ''; }
  logout(): void { this.authService.logout(); this.router.navigate(['/']); }
}
