import { Component, input, inject, output } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { KeyboardShortcutService } from '../../core/services/keyboard-shortcut.service';
import { LanguageSwitcherComponent } from '../../shared/components/language-switcher/language-switcher.component';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [TranslatePipe, LanguageSwitcherComponent],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.css'
})
export class TopbarComponent {
  pageTitle = input<string>('Dashboard');
  menuToggle = output<void>();
  readonly auth = inject(AuthService);
  readonly themeService = inject(ThemeService);
  readonly shortcutService = inject(KeyboardShortcutService);

  logout(): void {
    this.auth.logout();
  }

  onMenuToggle(): void {
    this.menuToggle.emit();
  }

  cycleTheme(): void {
    this.themeService.cycle();
  }

  openShortcuts(): void {
    this.shortcutService.openHelp();
  }
}
