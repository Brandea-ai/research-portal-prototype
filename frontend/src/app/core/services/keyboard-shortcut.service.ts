import { Injectable, signal, computed, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { fromEvent, timer } from 'rxjs';
import { filter, tap } from 'rxjs/operators';
import { ThemeService } from './theme.service';

/* ============================================
   Shortcut Model
   ============================================ */

export interface Shortcut {
  /** Tasten-Kombination als Display-String, z.B. "g d" oder "?" */
  readonly keys: string;
  /** Beschreibung der Aktion */
  readonly label: string;
  /** Gruppierung für das Hilfe-Overlay */
  readonly group: 'Navigation' | 'Aktionen' | 'System';
}

/* ============================================
   KeyboardShortcutService
   Bloomberg-Style 2-Tasten-Combos (vim/Gmail)
   ============================================ */

@Injectable({ providedIn: 'root' })
export class KeyboardShortcutService {

  private readonly router = inject(Router);
  private readonly themeService = inject(ThemeService);
  private readonly destroyRef = inject(DestroyRef);

  /** Alle registrierten Shortcuts */
  readonly shortcuts = signal<Shortcut[]>([
    // Navigation (g-Prefix)
    { keys: 'g d', label: 'Dashboard', group: 'Navigation' },
    { keys: 'g r', label: 'Research Reports', group: 'Navigation' },
    { keys: 'g s', label: 'Wertschriften', group: 'Navigation' },
    { keys: 'g a', label: 'Analysten', group: 'Navigation' },

    // Aktionen (n-Prefix)
    { keys: 'n r', label: 'Neuer Report', group: 'Aktionen' },

    // System
    { keys: 't', label: 'Theme wechseln', group: 'System' },
    { keys: '?', label: 'Shortcuts anzeigen', group: 'System' },
    { keys: 'Esc', label: 'Schliessen', group: 'System' },
  ]);

  /** Hilfe-Overlay offen/geschlossen */
  readonly isHelpOpen = signal(false);

  /** Aktueller Prefix für 2-Tasten-Combos */
  private readonly pendingPrefix = signal<string | null>(null);

  /** Gruppierte Shortcuts für das Hilfe-Overlay */
  readonly groupedShortcuts = computed(() => {
    const all = this.shortcuts();
    const groups = new Map<string, Shortcut[]>();
    for (const s of all) {
      const list = groups.get(s.group) ?? [];
      list.push(s);
      groups.set(s.group, list);
    }
    return groups;
  });

  constructor() {
    this.initKeyboardListener();
  }

  toggleHelp(): void {
    this.isHelpOpen.update(v => !v);
  }

  openHelp(): void {
    this.isHelpOpen.set(true);
  }

  closeHelp(): void {
    this.isHelpOpen.set(false);
  }

  /* ----------------------------------------
     Keyboard Event Stream (RxJS)
     ---------------------------------------- */

  private initKeyboardListener(): void {
    fromEvent<KeyboardEvent>(document, 'keydown').pipe(
      filter(e => !this.isInputFocused(e)),
      tap(e => this.handleKeydown(e)),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
  }

  /** Prüft ob ein Input-/Textarea-/Select-Element fokussiert ist */
  private isInputFocused(event: KeyboardEvent): boolean {
    const target = event.target as HTMLElement;
    if (!target) return false;
    const tag = target.tagName.toLowerCase();
    if (tag === 'input' || tag === 'textarea' || tag === 'select') return true;
    if (target.isContentEditable) return true;
    return false;
  }

  private handleKeydown(event: KeyboardEvent): void {
    const key = event.key;
    const prefix = this.pendingPrefix();

    // Escape: Hilfe schliessen, Prefix zurücksetzen
    if (key === 'Escape') {
      this.pendingPrefix.set(null);
      this.closeHelp();
      return;
    }

    // Hilfe: ? (Shift+/)
    if (key === '?') {
      event.preventDefault();
      this.toggleHelp();
      this.pendingPrefix.set(null);
      return;
    }

    // Wenn Hilfe offen ist, ignoriere andere Tasten (ausser Esc und ?)
    if (this.isHelpOpen()) return;

    // 2-Tasten-Combo: Zweiter Tastendruck
    if (prefix) {
      this.pendingPrefix.set(null);
      this.executeCombo(prefix, key, event);
      return;
    }

    // 1-Tasten-Shortcuts
    if (key === 't') {
      event.preventDefault();
      this.themeService.cycle();
      return;
    }

    // Prefix setzen für 2-Tasten-Combos
    if (key === 'g' || key === 'n') {
      this.pendingPrefix.set(key);
      // Timeout: Prefix verfällt nach 1500ms
      timer(1500).pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(() => {
        if (this.pendingPrefix() === key) {
          this.pendingPrefix.set(null);
        }
      });
      return;
    }
  }

  private executeCombo(prefix: string, key: string, event: KeyboardEvent): void {
    // g-Prefix: Navigation
    if (prefix === 'g') {
      switch (key) {
        case 'd':
          event.preventDefault();
          this.router.navigate(['/dashboard']);
          break;
        case 'r':
          event.preventDefault();
          this.router.navigate(['/reports']);
          break;
        case 's':
          event.preventDefault();
          this.router.navigate(['/securities']);
          break;
        case 'a':
          event.preventDefault();
          this.router.navigate(['/analysts']);
          break;
      }
      return;
    }

    // n-Prefix: Neue Objekte erstellen
    if (prefix === 'n') {
      switch (key) {
        case 'r':
          event.preventDefault();
          this.router.navigate(['/reports/new']);
          break;
      }
      return;
    }
  }
}
