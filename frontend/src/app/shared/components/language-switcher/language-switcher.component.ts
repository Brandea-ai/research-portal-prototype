import { Component, signal, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

type Language = 'de' | 'fr' | 'en';

const STORAGE_KEY = 'rp_lang';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [],
  templateUrl: './language-switcher.component.html',
  styleUrl: './language-switcher.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LanguageSwitcherComponent implements OnInit {
  private readonly translate = inject(TranslateService);

  readonly languages: Language[] = ['de', 'fr', 'en'];
  readonly currentLang = signal<Language>('de');

  ngOnInit(): void {
    const stored = localStorage.getItem(STORAGE_KEY) as Language | null;
    const initial: Language = stored && this.languages.includes(stored) ? stored : 'de';
    this.currentLang.set(initial);
    this.translate.use(initial);
  }

  switchLanguage(lang: Language): void {
    this.currentLang.set(lang);
    this.translate.use(lang);
    localStorage.setItem(STORAGE_KEY, lang);
  }
}
