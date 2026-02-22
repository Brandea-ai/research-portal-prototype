import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { AnalystService } from '../../core/services/analyst.service';
import { Analyst } from '../../core/models/analyst.model';

@Component({
  selector: 'app-analysts',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './analysts.component.html',
  styleUrl: './analysts.component.css'
})
export class AnalystsComponent implements OnInit {

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
}
