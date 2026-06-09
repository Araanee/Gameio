import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AppUser {
  id?: number;
  username: string;
  password?: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.apiUrl);
  }

  create(user: AppUser): Observable<AppUser> {
    return this.http.post<AppUser>(this.apiUrl, user);
  }

  update(id: number, user: AppUser): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.apiUrl}/${id}`, user);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
