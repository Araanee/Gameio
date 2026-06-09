import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export type Role = 'USER' | 'EDITOR' | 'WEBMASTER';

export interface AuthUser {
  username: string;
  password: string;
  role: Role;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser = signal<AuthUser | null>(null);
  private apiUrl = 'http://localhost:8080/api/auth/me';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<boolean> {
    const header = 'Basic ' + btoa(`${username}:${password}`);
    return this.http.get<{ username: string; role: string }>(this.apiUrl, {
      headers: new HttpHeaders({ Authorization: header })
    }).pipe(
      map(user => {
        this.currentUser.set({ username: user.username, password, role: user.role as Role });
        return true;
      }),
      catchError(() => of(false))
    );
  }

  logout(): void {
    this.currentUser.set(null);
  }

  getUser(): AuthUser | null {
    return this.currentUser();
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  hasRole(role: Role): boolean {
    return this.currentUser()?.role === role;
  }

  hasRoleOrHigher(role: Role): boolean {
    const user = this.currentUser();
    if (!user) return false;
    const levels: Record<Role, number> = { USER: 1, EDITOR: 2, WEBMASTER: 3 };
    return levels[user.role] >= levels[role];
  }

  register(username: string, password: string): Observable<{ success: boolean; error?: string }> {
    return this.http.post<any>('http://localhost:8080/api/auth/register', { username, password }).pipe(
      map(() => ({ success: true })),
      catchError(err => of({ success: false, error: err.error?.error || 'Erreur lors de l\'inscription.' }))
    );
  }

  getBasicAuthHeader(): string | null {
    const user = this.currentUser();
    if (!user) return null;
    return 'Basic ' + btoa(`${user.username}:${user.password}`);
  }
}
