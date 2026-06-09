import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GameService } from '../../core/services/game';
import { AuthService } from '../../core/services/auth.service';
import { BoardGame, Page } from '../../models/game.model';

@Component({
  selector: 'app-game-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './game-list.html',
  styleUrl: './game-list.css'
})
export class GameListComponent implements OnInit {

  games: BoardGame[] = [];
  loading = false;

  searchTitle = '';
  searchGenre = '';
  searchYear: number | undefined;
  searchPlayers: number | undefined;
  sortBy = 'title';
  direction = 'asc';

  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 12;

  constructor(private gameService: GameService, public authService: AuthService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchGenre = params['genre'] || '';
      this.currentPage = 0;
      this.loadGames();
    });
  }

  loadGames(): void {
    this.loading = true;
    this.gameService.getGames(this.searchTitle || undefined, this.searchGenre || undefined, this.searchYear, this.searchPlayers, this.currentPage, this.pageSize, this.sortBy, this.direction).subscribe({
      next: (page: Page<BoardGame>) => {
        this.games = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  search(): void {
    this.currentPage = 0;
    this.loadGames();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadGames();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  get visiblePages(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    if (total <= 7) return this.pages;
    const result: number[] = [0];
    if (current > 2) result.push(-1);
    for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) result.push(i);
    if (current < total - 3) result.push(-1);
    result.push(total - 1);
    return result;
  }
}
