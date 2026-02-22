import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../models/user.model';

const MOCK_USERS: Record<string, { password: string; user: User }> = {
  analyst: {
    password: 'analyst',
    user: {
      username: 'analyst',
      displayName: 'Dr. Lukas Meier',
      role: 'ANALYST',
      department: 'Equity Research'
    }
  },
  admin: {
    password: 'admin',
    user: {
      username: 'admin',
      displayName: 'Sarah Brunner',
      role: 'ADMIN',
      department: 'Research Management'
    }
  }
};

const TOKEN_KEY = 'rp_auth_token';
const USER_KEY = 'rp_auth_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly currentUser = signal<User | null>(this.loadStoredUser());

  readonly user = this.currentUser.asReadonly();
  readonly isLoggedIn = computed(() => this.currentUser() !== null);
  readonly displayName = computed(() => this.currentUser()?.displayName ?? '');

  constructor(private readonly router: Router) {}

  login(username: string, password: string): boolean {
    const entry = MOCK_USERS[username.toLowerCase()];
    if (!entry || entry.password !== password) {
      return false;
    }

    const token = btoa(`${username}:${Date.now()}`);
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(USER_KEY, JSON.stringify(entry.user));
    this.currentUser.set(entry.user);
    return true;
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  private loadStoredUser(): User | null {
    const json = sessionStorage.getItem(USER_KEY);
    if (!json) return null;
    try {
      return JSON.parse(json) as User;
    } catch {
      return null;
    }
  }
}
