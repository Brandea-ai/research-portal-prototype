import { Component, OnInit, signal, computed, ChangeDetectionStrategy, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AnalystService } from '../../core/services/analyst.service';
import { Analyst } from '../../core/models/analyst.model';

type SortOption =
  | 'name-asc'
  | 'name-desc'
  | 'accuracy-desc'
  | 'accuracy-asc'
  | 'rating-desc'
  | 'coverage-desc';

@Component({
  selector: 'app-analysts',
  standalone: true,
  imports: [DecimalPipe, FormsModule],
  templateUrl: './analysts.component.html',
  styleUrl: './analysts.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalystsComponent implements OnInit {

  private readonly router = inject(Router);

  analysts = signal<Analyst[]>([]);
  loading = signal(true);

  searchTerm = signal('');
  filterDepartment = signal('ALL');
  sortOption = signal<SortOption>('accuracy-desc');

  departments = computed(() => {
    const depts = new Set(this.analysts().map(a => a.department));
    return ['ALL', ...Array.from(depts).sort()];
  });

  activeFilterCount = computed(() => {
    let count = 0;
    if (this.filterDepartment() !== 'ALL') count++;
    if (this.searchTerm().trim().length > 0) count++;
    return count;
  });

  filteredAnalysts = computed(() => {
    let result = this.analysts();

    // Department filter
    const dept = this.filterDepartment();
    if (dept !== 'ALL') {
      result = result.filter(a => a.department === dept);
    }

    // Search filter
    const search = this.searchTerm().trim().toLowerCase();
    if (search.length > 0) {
      result = result.filter(a =>
        a.name.toLowerCase().includes(search) ||
        a.title.toLowerCase().includes(search) ||
        a.department.toLowerCase().includes(search) ||
        a.email.toLowerCase().includes(search) ||
        a.coverageUniverse.some(ticker => ticker.toLowerCase().includes(search))
      );
    }

    // Sort
    const sort = this.sortOption();
    result = [...result].sort((a, b) => {
      switch (sort) {
        case 'name-asc':
          return a.name.localeCompare(b.name);
        case 'name-desc':
          return b.name.localeCompare(a.name);
        case 'accuracy-desc':
          return b.accuracy12m - a.accuracy12m;
        case 'accuracy-asc':
          return a.accuracy12m - b.accuracy12m;
        case 'rating-desc':
          return b.starRating - a.starRating;
        case 'coverage-desc':
          return b.coverageUniverse.length - a.coverageUniverse.length;
        default:
          return 0;
      }
    });

    return result;
  });

  constructor(private readonly analystService: AnalystService) {}

  ngOnInit(): void {
    this.analystService.getAll().subscribe(data => {
      this.analysts.set(data);
      this.loading.set(false);
    });
  }

  starDisplay(rating: number): string {
    return '\u2605'.repeat(rating) + '\u2606'.repeat(5 - rating);
  }

  openAnalyst(id: number): void {
    this.router.navigate(['/analysts', id]);
  }

  resetFilters(): void {
    this.searchTerm.set('');
    this.filterDepartment.set('ALL');
  }

  formatDepartmentLabel(value: string): string {
    if (value === 'ALL') return 'Alle Abteilungen';
    return value;
  }
}
