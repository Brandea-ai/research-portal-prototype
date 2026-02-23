import { Component, OnInit, signal, ChangeDetectionStrategy, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import { AnalystService } from '../../core/services/analyst.service';
import { Analyst } from '../../core/models/analyst.model';

@Component({
  selector: 'app-analysts',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './analysts.component.html',
  styleUrl: './analysts.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalystsComponent implements OnInit {

  private readonly router = inject(Router);

  analysts = signal<Analyst[]>([]);
  loading = signal(true);

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
}
