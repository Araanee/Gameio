import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GameService } from '../../core/services/game';
import { BoardGame } from '../../models/game.model';

@Component({
  selector: 'app-my-proposals',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './my-proposals.html',
  styleUrl: './my-proposals.css'
})
export class MyProposalsComponent implements OnInit {

  proposals: BoardGame[] = [];
  loading = false;
  editingGame: BoardGame | null = null;
  gameToDelete: BoardGame | null = null;
  message = '';

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.gameService.getMyProposals().subscribe({
      next: (data) => { this.proposals = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  startEdit(game: BoardGame): void {
    this.editingGame = { ...game };
  }

  cancelEdit(): void {
    this.editingGame = null;
  }

  saveEdit(): void {
    if (!this.editingGame) return;
    this.gameService.updateGame(this.editingGame.id!, this.editingGame).subscribe({
      next: () => {
        this.message = 'Proposition mise à jour.';
        this.editingGame = null;
        this.load();
      },
      error: () => { this.message = 'Erreur lors de la mise à jour.'; }
    });
  }

  confirmDelete(game: BoardGame): void { this.gameToDelete = game; }
  cancelDelete(): void { this.gameToDelete = null; }

  deleteGame(): void {
    if (!this.gameToDelete) return;
    const game = this.gameToDelete;
    this.gameToDelete = null;
    this.gameService.deleteGame(game.id!).subscribe({
      next: () => { this.message = `"${game.title}" supprimé.`; this.load(); },
      error: () => { this.message = 'Erreur lors de la suppression.'; }
    });
  }

  statusLabel(status: string | undefined): string {
    switch (status) {
      case 'APPROVED': return 'Approuvé';
      case 'REJECTED': return 'Refusé';
      default: return 'En attente';
    }
  }
}
