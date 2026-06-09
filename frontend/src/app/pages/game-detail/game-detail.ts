import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { GameService } from '../../core/services/game';
import { RatingService } from '../../core/services/rating';
import { AuthService } from '../../core/services/auth.service';
import { BoardGame } from '../../models/game.model';
import { Rating } from '../../models/rating.model';

@Component({
  selector: 'app-game-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './game-detail.html',
  styleUrl: './game-detail.css'
})
export class GameDetailComponent implements OnInit {

  game: BoardGame | null = null;
  loading = false;
  ratingError = '';

  newRating: Rating = { boardGameId: 0, score: 5, comment: '' };
  ratingSubmitted = false;

  ratings: Rating[] = [];
  editingRating: Rating | null = null;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private ratingService: RatingService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.newRating.boardGameId = id;
    this.loading = true;
    this.gameService.getGameById(id).subscribe({
      next: (data) => {
        this.game = data;
        this.loading = false;
        this.ratingService.getRatings(id).subscribe({
          next: (r) => { this.ratings = r; },
          error: () => {}
        });
      },
      error: () => { this.loading = false; }
    });
  }

  ratingCount(star: number): number {
    return this.ratings.filter(r => r.score === star).length;
  }

  ratingPercent(star: number): number {
    if (!this.ratings.length) return 0;
    return (this.ratingCount(star) / this.ratings.length) * 100;
  }

  isOwnRating(rating: Rating): boolean {
    return this.authService.isLoggedIn() && this.authService.getUser()?.username === rating.userId;
  }

  startEditRating(rating: Rating): void {
    this.editingRating = { ...rating };
  }

  cancelEditRating(): void {
    this.editingRating = null;
  }

  saveEditRating(): void {
    if (!this.editingRating || !this.game) return;
    this.ratingService.updateRating(this.game.id!, this.editingRating.id!, this.editingRating).subscribe({
      next: () => {
        this.editingRating = null;
        this.reloadRatingsAndGame();
      },
      error: () => { this.ratingError = 'Erreur lors de la modification.'; }
    });
  }

  deleteRating(rating: Rating): void {
    if (!this.game) return;
    this.ratingService.deleteRating(this.game.id!, rating.id!).subscribe({
      next: () => { this.reloadRatingsAndGame(); },
      error: () => { this.ratingError = 'Erreur lors de la suppression.'; }
    });
  }

  private reloadRatingsAndGame(): void {
    const id = this.game!.id!;
    forkJoin({
      ratings: this.ratingService.getRatings(id),
      game: this.gameService.getGameById(id)
    }).subscribe({
      next: ({ ratings, game }) => { this.ratings = ratings; this.game = game; },
      error: () => { this.ratingError = 'Erreur lors du chargement.'; }
    });
  }

  submitRating(): void {
    this.ratingError = '';
    const user = this.authService.getUser();
    if (user) this.newRating.userId = user.username;

    this.ratingService.rateGame(this.newRating.boardGameId, this.newRating).subscribe({
      next: () => { this.ratingSubmitted = true; },
      error: (err) => {
        if (err.status === 401 || err.status === 403) {
          this.ratingError = 'Vous devez être connecté pour noter un jeu.';
        } else if (err.status === 409) {
          this.ratingError = 'Vous avez déjà noté ce jeu.';
        } else {
          this.ratingError = 'Une erreur est survenue, réessayez.';
        }
      }
    });
  }
}
