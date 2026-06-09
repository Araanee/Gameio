import { Routes } from '@angular/router';
import { GameListComponent } from './pages/game-list/game-list';
import { GameDetailComponent } from './pages/game-detail/game-detail';
import { GameProposeComponent } from './pages/game-propose/game-propose';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { AdminComponent } from './pages/admin/admin';
import { MyProposalsComponent } from './pages/my-proposals/my-proposals';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: GameListComponent },
  { path: 'games/:id', component: GameDetailComponent },
  { path: 'propose', component: GameProposeComponent, canActivate: [authGuard('EDITOR')] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'admin', component: AdminComponent, canActivate: [authGuard('WEBMASTER')] },
  { path: 'my-proposals', component: MyProposalsComponent, canActivate: [authGuard('EDITOR')] },
  { path: '**', redirectTo: '' }
];
