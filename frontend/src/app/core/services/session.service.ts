import {
  Injectable,
  signal,
  effect,
  inject,
  OnDestroy,
  PLATFORM_ID
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

interface SessionStatusResponse {
  active: boolean;
  expiresInSeconds: number;
  maxInactiveInterval: number;
}

/**
 * Service für Session-Timeout-Management und Keep-Alive.
 *
 * Verwaltet den Session-Timeout im Frontend synchron zum Backend:
 * - Überwacht Benutzeraktivität (Mausbewegung, Tastendruck)
 * - Sendet periodische Keep-Alive-Requests zum Backend (alle 5 Minuten)
 * - Zeigt eine Warnung wenn weniger als 5 Minuten verbleiben
 * - Führt automatischen Logout durch bei Timeout
 *
 * Signal-basiert für optimale Change-Detection-Kompatibilität.
 */
@Injectable({ providedIn: 'root' })
export class SessionService implements OnDestroy {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly platformId = inject(PLATFORM_ID);

  /** Konfigurierter Session-Timeout in Minuten (Standard: 30) */
  readonly sessionTimeoutMinutes = signal(30);

  /** Verbleibende Sekunden bis zum Session-Timeout */
  readonly remainingSeconds = signal(0);

  /** Warnung ist sichtbar wenn weniger als 5 Minuten verbleiben */
  readonly isWarningVisible = signal(false);

  private readonly WARNING_THRESHOLD_SECONDS = 5 * 60; // 5 Minuten
  private readonly KEEPALIVE_INTERVAL_MS = 5 * 60 * 1000; // 5 Minuten

  private lastActivityTime = Date.now();
  private keepAliveTimer: ReturnType<typeof setInterval> | null = null;
  private countdownTimer: ReturnType<typeof setInterval> | null = null;
  private activityListeners: Array<{ event: string; handler: () => void }> = [];

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.setupActivityDetection();
      this.startKeepAliveTimer();
      this.startCountdownTimer();

      // Session-Überwachung startet nur wenn eingeloggt
      effect(() => {
        const loggedIn = this.authService.isLoggedIn();
        if (loggedIn) {
          this.resetSessionTimer();
        } else {
          this.stopAllTimers();
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.stopAllTimers();
    this.removeActivityListeners();
  }

  /**
   * Setzt den Session-Timer zurück bei Benutzeraktivität.
   * Wird auch manuell aufgerufen wenn der Benutzer "Verlängern" klickt.
   */
  resetSessionTimer(): void {
    this.lastActivityTime = Date.now();
    const totalSeconds = this.sessionTimeoutMinutes() * 60;
    this.remainingSeconds.set(totalSeconds);
    this.isWarningVisible.set(false);
  }

  /**
   * Verlängert die Session explizit via Keep-Alive-Request zum Backend.
   * Wird aufgerufen wenn der Benutzer auf "Verlängern" klickt.
   */
  extendSession(): void {
    this.sendKeepAlive();
    this.resetSessionTimer();
  }

  /**
   * Richtet Activity-Detection für Mausbewegung und Tastendruck ein.
   */
  private setupActivityDetection(): void {
    const activityEvents = ['mousemove', 'keydown', 'click', 'touchstart'];

    const activityHandler = (): void => {
      const now = Date.now();
      // Debounce: Aktivität nur alle 10 Sekunden registrieren
      if (now - this.lastActivityTime > 10_000) {
        this.lastActivityTime = now;
      }
    };

    activityEvents.forEach(eventName => {
      document.addEventListener(eventName, activityHandler, { passive: true });
      this.activityListeners.push({ event: eventName, handler: activityHandler });
    });
  }

  /**
   * Startet den periodischen Keep-Alive-Timer (alle 5 Minuten).
   * Keep-Alive wird nur gesendet wenn der Benutzer aktiv war.
   */
  private startKeepAliveTimer(): void {
    this.keepAliveTimer = setInterval(() => {
      if (!this.authService.isLoggedIn()) return;

      const inactiveMs = Date.now() - this.lastActivityTime;
      const inactiveMinutes = inactiveMs / 60_000;

      // Keep-Alive nur senden wenn Benutzer in den letzten 5 Minuten aktiv war
      if (inactiveMinutes < 5) {
        this.sendKeepAlive();
      }
    }, this.KEEPALIVE_INTERVAL_MS);
  }

  /**
   * Startet den sekundenweisen Countdown-Timer für die Warnanzeige.
   */
  private startCountdownTimer(): void {
    const totalSeconds = this.sessionTimeoutMinutes() * 60;
    this.remainingSeconds.set(totalSeconds);

    this.countdownTimer = setInterval(() => {
      if (!this.authService.isLoggedIn()) return;

      const elapsedSeconds = Math.floor((Date.now() - this.lastActivityTime) / 1000);
      const total = this.sessionTimeoutMinutes() * 60;
      const remaining = Math.max(0, total - elapsedSeconds);

      this.remainingSeconds.set(remaining);

      // Warnung einblenden wenn weniger als 5 Minuten verbleiben
      if (remaining <= this.WARNING_THRESHOLD_SECONDS && remaining > 0) {
        this.isWarningVisible.set(true);
      } else {
        this.isWarningVisible.set(false);
      }

      // Automatischer Logout bei Timeout
      if (remaining === 0) {
        this.handleSessionTimeout();
      }
    }, 1000);
  }

  /**
   * Sendet einen Keep-Alive-Request zum Backend.
   */
  private sendKeepAlive(): void {
    this.http
      .post<{ extended: boolean; maxInactiveInterval: number }>(
        `${environment.apiUrl}/session/keepalive`,
        {}
      )
      .pipe(
        catchError(() => of(null))
      )
      .subscribe(response => {
        if (response?.maxInactiveInterval) {
          const minutes = Math.floor(response.maxInactiveInterval / 60);
          this.sessionTimeoutMinutes.set(minutes);
        }
      });
  }

  /**
   * Behandelt den Session-Timeout: Logout und Redirect zu /login.
   */
  private handleSessionTimeout(): void {
    this.stopAllTimers();
    this.isWarningVisible.set(false);
    this.authService.logout();
  }

  /**
   * Stoppt alle aktiven Timer.
   */
  private stopAllTimers(): void {
    if (this.keepAliveTimer !== null) {
      clearInterval(this.keepAliveTimer);
      this.keepAliveTimer = null;
    }
    if (this.countdownTimer !== null) {
      clearInterval(this.countdownTimer);
      this.countdownTimer = null;
    }
  }

  /**
   * Entfernt alle Event-Listener für Activity-Detection.
   */
  private removeActivityListeners(): void {
    this.activityListeners.forEach(({ event, handler }) => {
      document.removeEventListener(event, handler);
    });
    this.activityListeners = [];
  }
}
