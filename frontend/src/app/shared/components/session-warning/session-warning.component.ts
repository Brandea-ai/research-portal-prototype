import { Component, inject, computed, ChangeDetectionStrategy } from '@angular/core';
import { SessionService } from '../../../core/services/session.service';

/**
 * Session-Timeout-Warnkomponente.
 *
 * Zeigt eine Warnung an wenn die Session in weniger als 5 Minuten abläuft.
 * Bietet dem Benutzer die Möglichkeit die Session zu verlängern.
 *
 * Standalone-Komponente, wird im App Shell eingebunden.
 * Signal-basierte State-Verwaltung für OnPush Change Detection.
 */
@Component({
  selector: 'app-session-warning',
  standalone: true,
  templateUrl: './session-warning.component.html',
  styleUrl: './session-warning.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionWarningComponent {

  readonly sessionService = inject(SessionService);

  /** Verbleibende Minuten (aufgerundet) für die Anzeige */
  readonly remainingMinutes = computed(() => {
    return Math.ceil(this.sessionService.remainingSeconds() / 60);
  });

  /** Verbleibende Sekunden innerhalb der aktuellen Minute */
  readonly remainingSecondsInMinute = computed(() => {
    return this.sessionService.remainingSeconds() % 60;
  });

  /** Formatierte Anzeige der verbleibenden Zeit (MM:SS) */
  readonly timeDisplay = computed(() => {
    const totalSeconds = this.sessionService.remainingSeconds();
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  });

  /** Verlängert die Session bei Klick auf "Verlängern" */
  onExtendSession(): void {
    this.sessionService.extendSession();
  }
}
