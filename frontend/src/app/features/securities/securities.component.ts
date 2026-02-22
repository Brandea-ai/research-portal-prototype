import { Component, OnInit, signal } from '@angular/core';
import { SecurityService } from '../../core/services/security.service';
import { Security } from '../../core/models/security.model';

@Component({
  selector: 'app-securities',
  standalone: true,
  templateUrl: './securities.component.html',
  styleUrl: './securities.component.css'
})
export class SecuritiesComponent implements OnInit {

  securities = signal<Security[]>([]);
  loading = signal(true);

  constructor(private readonly securityService: SecurityService) {}

  ngOnInit(): void {
    this.securityService.getAll().subscribe(data => {
      this.securities.set(data);
      this.loading.set(false);
    });
  }

  formatMarketCap(value: number): string {
    if (value >= 1_000_000_000_000) return (value / 1_000_000_000_000).toFixed(1) + ' Bio.';
    if (value >= 1_000_000_000) return (value / 1_000_000_000).toFixed(1) + ' Mrd.';
    if (value >= 1_000_000) return (value / 1_000_000).toFixed(1) + ' Mio.';
    return value.toFixed(0);
  }
}
