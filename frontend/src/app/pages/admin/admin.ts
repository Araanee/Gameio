import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GameService } from '../../core/services/game';
import { UserService, AppUser } from '../../core/services/user.service';
import { BoardGame, Page } from '../../models/game.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent implements OnInit {

  pendingGames: BoardGame[] = [];
  approvedGames: BoardGame[] = [];
  loading = false;
  message = '';

editingGame: BoardGame | null = null;
  gameToDelete: BoardGame | null = null;

  users: AppUser[] = [];
  userToDelete: AppUser | null = null;
  editingUser: AppUser | null = null;
  showUserForm = false;
  newUser: AppUser = { username: '', password: '', role: 'USER' };

  readonly roles = ['USER', 'EDITOR', 'WEBMASTER'];

  constructor(private gameService: GameService, private userService: UserService) {}

  ngOnInit(): void {
    this.loadPending();
    this.loadApproved();
    this.loadUsers();
  }

loadPending(): void {
    this.loading = true;
    this.gameService.getPendingGames().subscribe({
      next: (data) => { this.pendingGames = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadApproved(): void {
    this.gameService.getGames(undefined, undefined, undefined, undefined, 0, 100).subscribe({
      next: (page: Page<BoardGame>) => { this.approvedGames = page.content; },
      error: () => {}
    });
  }

  approve(game: BoardGame): void {
    this.gameService.updateStatus(game.id!, 'APPROVED').subscribe({
      next: () => {
        this.message = `"${game.title}" approuvé.`;
        this.pendingGames = this.pendingGames.filter(g => g.id !== game.id);
        this.loadApproved();
      },
      error: () => { this.message = 'Erreur lors de l\'approbation.'; }
    });
  }

  reject(game: BoardGame): void {
    this.gameService.updateStatus(game.id!, 'REJECTED').subscribe({
      next: () => {
        this.message = `"${game.title}" rejeté.`;
        this.pendingGames = this.pendingGames.filter(g => g.id !== game.id);
      },
      error: () => { this.message = 'Erreur lors du rejet.'; }
    });
  }

  startEdit(game: BoardGame): void { this.editingGame = { ...game }; }
  cancelEdit(): void { this.editingGame = null; }

  saveEdit(): void {
    if (!this.editingGame) return;
    this.gameService.updateGame(this.editingGame.id!, this.editingGame).subscribe({
      next: () => {
        this.message = `"${this.editingGame!.title}" mis à jour.`;
        this.editingGame = null;
        this.loadApproved();
      },
      error: () => { this.message = 'Erreur lors de la mise à jour.'; }
    });
  }

  confirmDelete(game: BoardGame): void { this.gameToDelete = game; }
  cancelDelete(): void { this.gameToDelete = null; }

  delete(): void {
    if (!this.gameToDelete) return;
    const game = this.gameToDelete;
    this.gameToDelete = null;
    this.gameService.deleteGame(game.id!).subscribe({
      next: () => { this.message = `"${game.title}" supprimé.`; this.loadApproved(); },
      error: () => { this.message = 'Erreur lors de la suppression.'; }
    });
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => { this.users = data; },
      error: () => {}
    });
  }

  openUserForm(): void {
    this.newUser = { username: '', password: '', role: 'USER' };
    this.showUserForm = true;
  }

  cancelUserForm(): void { this.showUserForm = false; }

  createUser(): void {
    if (!this.newUser.username || !this.newUser.password) return;
    this.userService.create(this.newUser).subscribe({
      next: () => {
        this.message = `Utilisateur "${this.newUser.username}" créé.`;
        this.showUserForm = false;
        this.loadUsers();
      },
      error: (err) => { this.message = err?.error?.message || 'Erreur lors de la création.'; }
    });
  }

  startEditUser(user: AppUser): void { this.editingUser = { ...user, password: '' }; }
  cancelEditUser(): void { this.editingUser = null; }

  saveEditUser(): void {
    if (!this.editingUser) return;
    this.userService.update(this.editingUser.id!, this.editingUser).subscribe({
      next: () => {
        this.message = `Utilisateur "${this.editingUser!.username}" mis à jour.`;
        this.editingUser = null;
        this.loadUsers();
      },
      error: () => { this.message = 'Erreur lors de la mise à jour.'; }
    });
  }

  confirmDeleteUser(user: AppUser): void { this.userToDelete = user; }
  cancelDeleteUser(): void { this.userToDelete = null; }

  deleteUser(): void {
    if (!this.userToDelete) return;
    const user = this.userToDelete;
    this.userToDelete = null;
    this.userService.delete(user.id!).subscribe({
      next: () => { this.message = `Utilisateur "${user.username}" supprimé.`; this.loadUsers(); },
      error: () => { this.message = 'Erreur lors de la suppression.'; }
    });
  }

  roleLabel(role: string): string {
    switch (role) {
      case 'WEBMASTER': return 'Admin';
      case 'EDITOR': return 'Éditeur';
      default: return 'Utilisateur';
    }
  }
}
