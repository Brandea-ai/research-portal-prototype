import { Injectable, signal, computed, effect } from '@angular/core';

export type Theme = 'light' | 'dark' | 'system';

const STORAGE_KEY = 'rp_theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {

  readonly theme = signal<Theme>(this.loadStored());

  readonly resolvedTheme = computed<'light' | 'dark'>(() => {
    const t = this.theme();
    if (t !== 'system') return t;
    return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
  });

  readonly icon = computed(() => {
    const t = this.theme();
    if (t === 'light') return '\u2600';   // Sun
    if (t === 'dark') return '\u263E';    // Moon
    return '\u25D0';                       // Half circle (system)
  });

  readonly label = computed(() => {
    const t = this.theme();
    if (t === 'light') return 'Hell';
    if (t === 'dark') return 'Dunkel';
    return 'System';
  });

  constructor() {
    effect(() => {
      const t = this.theme();
      document.documentElement.setAttribute('data-theme', t);
      localStorage.setItem(STORAGE_KEY, t);
    });

    // Listen for system preference changes
    window.matchMedia('(prefers-color-scheme: light)').addEventListener('change', () => {
      if (this.theme() === 'system') {
        // Force recompute by toggling back
        this.theme.set('system');
      }
    });
  }

  cycle(): void {
    const current = this.theme();
    const next: Theme = current === 'dark' ? 'light' : current === 'light' ? 'system' : 'dark';
    this.theme.set(next);
  }

  private loadStored(): Theme {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored === 'light' || stored === 'dark' || stored === 'system') return stored;
    return 'system';
  }
}
