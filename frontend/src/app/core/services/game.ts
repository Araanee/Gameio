import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BoardGame, Page } from '../../models/game.model';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private apiUrl = 'http://localhost:8080/api/games';

  constructor(private http: HttpClient) {}

  getGames(title?: string, genre?: string, year?: number, players?: number, page = 0, size = 12, sortBy = 'title', direction = 'asc'): Observable<Page<BoardGame>> {
    let params = new HttpParams().set('page', page).set('size', size).set('sortBy', sortBy).set('direction', direction);
    if (title) params = params.set('title', title);
    if (genre) params = params.set('genre', genre);
    if (year) params = params.set('year', year);
    if (players) params = params.set('players', players);
    return this.http.get<Page<BoardGame>>(this.apiUrl, { params });
  }

  getMyProposals(): Observable<BoardGame[]> {
    return this.http.get<BoardGame[]>(`${this.apiUrl}/my-proposals`);
  }

  getGameById(id: number): Observable<BoardGame> {
    return this.http.get<BoardGame>(`${this.apiUrl}/${id}`);
  }

  proposeGame(game: BoardGame): Observable<BoardGame> {
    return this.http.post<BoardGame>(this.apiUrl, game);
  }

  getPendingGames(): Observable<BoardGame[]> {
    return this.http.get<BoardGame[]>(`${this.apiUrl}/pending`);
  }

  updateStatus(id: number, status: string): Observable<BoardGame> {
    return this.http.patch<BoardGame>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }

  deleteGame(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateGame(id: number, game: BoardGame): Observable<BoardGame> {
    return this.http.put<BoardGame>(`${this.apiUrl}/${id}`, game);
  }

}
