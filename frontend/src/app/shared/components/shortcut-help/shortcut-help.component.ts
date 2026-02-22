import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { KeyboardShortcutService } from '../../../core/services/keyboard-shortcut.service';

@Component({
  selector: 'app-shortcut-help',
  standalone: true,
  imports: [KeyValuePipe],
  templateUrl: './shortcut-help.component.html',
  styleUrl: './shortcut-help.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ShortcutHelpComponent {
  readonly shortcutService = inject(KeyboardShortcutService);

  /** Schliesst bei Klick auf den Overlay-Hintergrund */
  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('shortcut-overlay')) {
      this.shortcutService.closeHelp();
    }
  }

  /** Teilt "g d" in ["g", "d"] für die kbd-Darstellung */
  splitKeys(keys: string): string[] {
    return keys.split(' ');
  }
}
