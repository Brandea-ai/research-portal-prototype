import {
  Component,
  OnInit,
  AfterViewInit,
  inject,
  signal,
  computed,
  effect,
  ElementRef,
  viewChild,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { ReportStateService } from '../../core/services/report-state.service';
import { SecurityService } from '../../core/services/security.service';
import { AnalystService } from '../../core/services/analyst.service';
import { ThemeService } from '../../core/services/theme.service';
import { Report } from '../../core/models/report.model';
import { Security } from '../../core/models/security.model';
import { Analyst } from '../../core/models/analyst.model';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit, AfterViewInit {
  reports = signal<Report[]>([]);
  securities = signal<Security[]>([]);
  analysts = signal<Analyst[]>([]);
  loading = signal(true);
  chartsReady = signal(false);

  private ratingChart: Chart | null = null;
  private sectorChart: Chart | null = null;

  ratingCanvasRef = viewChild<ElementRef<HTMLCanvasElement>>('ratingCanvas');
  sectorCanvasRef = viewChild<ElementRef<HTMLCanvasElement>>('sectorCanvas');

  // --- Computed Signals ---

  ratingChangedCount = computed(() => this.reports().filter((r) => r.ratingChanged).length);

  latestReports = computed(() =>
    [...this.reports()]
      .sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
      .slice(0, 8),
  );

  topAnalysts = computed(() =>
    [...this.analysts()]
      .sort((a, b) => b.accuracy12m - a.accuracy12m)
      .slice(0, 5),
  );

  sectorCount = computed(() => {
    const sectors = new Set(this.securities().map((s) => s.sector));
    return sectors.size;
  });

  avgAccuracy = computed(() => {
    const list = this.analysts();
    if (list.length === 0) return 0;
    const sum = list.reduce((acc, a) => acc + a.accuracy12m, 0);
    return sum / list.length;
  });

  totalCoverage = computed(() => {
    const tickers = new Set(this.analysts().flatMap((a) => a.coverageUniverse));
    return tickers.size;
  });

  ratingDistribution = computed(() => {
    const reports = this.reports();
    const buy = reports.filter(
      (r) => r.rating === 'STRONG_BUY' || r.rating === 'BUY',
    ).length;
    const hold = reports.filter((r) => r.rating === 'HOLD').length;
    const sell = reports.filter(
      (r) => r.rating === 'SELL' || r.rating === 'STRONG_SELL',
    ).length;
    return { buy, hold, sell };
  });

  sectorDistribution = computed(() => {
    const reportMap = new Map<string, number>();
    const reports = this.reports();
    const securities = this.securities();

    reports.forEach((report) => {
      const sec = securities.find((s) => s.id === report.securityId);
      if (sec?.sector) {
        reportMap.set(sec.sector, (reportMap.get(sec.sector) ?? 0) + 1);
      }
    });

    return [...reportMap.entries()]
      .sort((a, b) => b[1] - a[1])
      .slice(0, 7)
      .map(([sector, count]) => ({ sector, count }));
  });

  private readonly reportState = inject(ReportStateService);
  private readonly securityService = inject(SecurityService);
  private readonly analystService = inject(AnalystService);
  private readonly themeService = inject(ThemeService);

  constructor() {
    // Rebuild charts whenever the resolved theme changes so colors update instantly.
    effect(() => {
      this.themeService.resolvedTheme(); // track signal
      if (!this.loading()) {
        this.buildCharts();
      }
    });
  }

  ngOnInit(): void {
    this.reportState.reports$.subscribe((data) => {
      this.reports.set(data);
      if (!this.loading()) {
        this.buildCharts();
      }
    });
    this.reportState.loadReports();

    this.securityService.getAll().subscribe((data) => {
      this.securities.set(data);
      if (!this.loading()) {
        this.buildCharts();
      }
    });

    this.analystService.getAll().subscribe((data) => {
      this.analysts.set(data);
      this.loading.set(false);
      // All data is loaded — build charts after Angular renders the canvases.
      setTimeout(() => this.buildCharts(), 0);
    });
  }

  ngAfterViewInit(): void {
    this.chartsReady.set(true);
  }

  /**
   * Returns theme-appropriate colour tokens for Chart.js configs.
   * All values mirror the CSS custom properties defined in styles.css.
   */
  private getChartColors() {
    const isDark = this.themeService.resolvedTheme() === 'dark';
    return {
      positive:       isDark ? '#2DD4A0' : '#00875A',
      neutral:        isDark ? '#94A3B8' : '#64748B',
      negative:       isDark ? '#F87171' : '#CC2936',
      accent:         isDark ? '#4DA3FF' : '#0062CC',
      accentBg:       isDark ? 'rgba(77, 163, 255, 0.15)' : 'rgba(0, 98, 204, 0.12)',
      accentBgHover:  isDark ? 'rgba(77, 163, 255, 0.28)' : 'rgba(0, 98, 204, 0.22)',
      accentBorder:   isDark ? '#4DA3FF' : '#0062CC',
      tooltipBg:      isDark ? '#1A1E28' : '#FFFFFF',
      tooltipBorder:  isDark ? '#333C4D' : '#DFE2E8',
      tooltipTitle:   isDark ? '#9BA3B5' : '#505868',
      tooltipBody:    isDark ? '#ECEEF2' : '#1A1D24',
      gridColor:      isDark ? '#262D3A' : '#DFE2E8',
      tickColor:      isDark ? '#6B7588' : '#7A8294',
      tickLabelColor: isDark ? '#9BA3B5' : '#505868',
    };
  }

  private buildCharts(): void {
    this.buildRatingChart();
    this.buildSectorChart();
  }

  private buildRatingChart(): void {
    const canvasRef = this.ratingCanvasRef();
    if (!canvasRef) return;

    if (this.ratingChart) {
      this.ratingChart.destroy();
    }

    const { buy, hold, sell } = this.ratingDistribution();
    const ctx = canvasRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const colors = this.getChartColors();

    this.ratingChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Buy / Strong Buy', 'Hold', 'Sell / Strong Sell'],
        datasets: [
          {
            data: [buy, hold, sell],
            backgroundColor: [colors.positive, colors.neutral, colors.negative],
            borderColor: 'transparent',
            borderWidth: 0,
            hoverOffset: 6,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '72%',
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: colors.tooltipBg,
            borderColor: colors.tooltipBorder,
            borderWidth: 1,
            titleColor: colors.tooltipTitle,
            bodyColor: colors.tooltipBody,
            titleFont: { family: 'Inter', size: 11, weight: 'normal' },
            bodyFont: { family: 'JetBrains Mono, monospace', size: 13, weight: 'bold' },
            padding: 12,
            displayColors: true,
            boxWidth: 8,
            boxHeight: 8,
            callbacks: {
              label: (ctx) => `  ${ctx.formattedValue} Reports`,
            },
          },
        },
        animation: {
          duration: 800,
          easing: 'easeInOutQuart',
        },
      },
    });
  }

  private buildSectorChart(): void {
    const canvasRef = this.sectorCanvasRef();
    if (!canvasRef) return;

    if (this.sectorChart) {
      this.sectorChart.destroy();
    }

    const data = this.sectorDistribution();
    if (data.length === 0) return;

    const ctx = canvasRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const colors = this.getChartColors();

    this.sectorChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map((d) => d.sector),
        datasets: [
          {
            label: 'Reports',
            data: data.map((d) => d.count),
            backgroundColor: colors.accentBg,
            borderColor: colors.accentBorder,
            borderWidth: 1,
            borderRadius: 3,
            hoverBackgroundColor: colors.accentBgHover,
            hoverBorderColor: colors.accentBorder,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: colors.tooltipBg,
            borderColor: colors.tooltipBorder,
            borderWidth: 1,
            titleColor: colors.tooltipTitle,
            bodyColor: colors.tooltipBody,
            titleFont: { family: 'Inter', size: 11, weight: 'normal' },
            bodyFont: { family: 'JetBrains Mono, monospace', size: 13, weight: 'bold' },
            padding: 12,
            displayColors: false,
            callbacks: {
              label: (ctx) => `${ctx.formattedValue} Reports`,
            },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            border: { display: false },
            ticks: {
              color: colors.tickColor,
              font: { family: 'JetBrains Mono, monospace', size: 11 },
              maxTicksLimit: 6,
            },
          },
          y: {
            grid: { display: false },
            border: { display: false },
            ticks: {
              color: colors.tickLabelColor,
              font: { family: 'Inter', size: 12 },
              padding: 8,
            },
          },
        },
        animation: {
          duration: 700,
          easing: 'easeInOutQuart',
        },
      },
    });
  }

  ratingClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  }

  formatRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  starDisplay(starRating: number): string {
    const full = Math.round(starRating);
    const empty = 5 - full;
    return '\u2605'.repeat(full) + '\u2606'.repeat(empty);
  }

  impliedUpsideClass(upside: number): string {
    if (upside > 0) return 'positive';
    if (upside < 0) return 'negative';
    return 'neutral';
  }
}
