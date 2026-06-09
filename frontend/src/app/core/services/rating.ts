import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rating } from '../../models/rating.model';

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  private apiUrl = 'http://localhost:8080/api/games';

  constructor(private http: HttpClient) {}

  rateGame(gameId: number, rating: Rating): Observable<Rating> {
    return this.http.post<Rating>(`${this.apiUrl}/${gameId}/ratings`, rating);
  }

  getRatings(gameId: number): Observable<Rating[]> {
    return this.http.get<Rating[]>(`${this.apiUrl}/${gameId}/ratings`);
  }

  updateRating(gameId: number, ratingId: number, rating: Rating): Observable<Rating> {
    return this.http.put<Rating>(`${this.apiUrl}/${gameId}/ratings/${ratingId}`, rating);
  }

  deleteRating(gameId: number, ratingId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${gameId}/ratings/${ratingId}`);
  }
}