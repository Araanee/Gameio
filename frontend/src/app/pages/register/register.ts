import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username = '';
  password = '';
  confirmPassword = '';
  error = '';
  success = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    this.error = '';
    if (!this.username || !this.password || !this.confirmPassword) {
      this.error = 'Veuillez remplir tous les champs.';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas.';
      return;
    }
    if (this.password.length < 4) {
      this.error = 'Le mot de passe doit faire au moins 4 caractères.';
      return;
    }
    this.loading = true;
    this.authService.register(this.username.trim(), this.password).subscribe(res => {
      if (res.success) {
        this.authService.login(this.username.trim(), this.password).subscribe(() => {
          this.router.navigate(['/']);
        });
      } else {
        this.loading = false;
        this.error = res.error || 'Erreur lors de l\'inscription.';
      }
    });
  }
}
