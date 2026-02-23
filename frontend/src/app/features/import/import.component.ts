import {
  Component,
  OnInit,
  inject,
  signal,
  ChangeDetectionStrategy,
} from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb.component';
import { ImportService, XmlImportResponse, XmlValidationResponse } from '../../core/services/import.service';

@Component({
  selector: 'app-import',
  standalone: true,
  imports: [TranslatePipe, BreadcrumbComponent],
  templateUrl: './import.component.html',
  styleUrl: './import.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImportComponent implements OnInit {

  private readonly importService = inject(ImportService);

  /* ---- Breadcrumb ---- */
  readonly breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Import' },
  ];

  /* ---- Signals ---- */
  readonly selectedFile = signal<File | null>(null);
  readonly validationResult = signal<XmlValidationResponse | null>(null);
  readonly importResult = signal<XmlImportResponse | null>(null);
  readonly tickerFilter = signal<string>('');
  readonly loading = signal<boolean>(false);
  readonly sampleXml = signal<string>('');
  readonly showSample = signal<boolean>(false);
  readonly dragOver = signal<boolean>(false);

  ngOnInit(): void {
    // Component initialized; sample XML loaded on demand
  }

  /* ---- Drag & Drop ---- */

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver.set(false);

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const file = files[0];
      if (file.name.endsWith('.xml')) {
        this.selectFile(file);
      }
    }
  }

  onFileInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectFile(input.files[0]);
    }
  }

  removeFile(): void {
    this.selectedFile.set(null);
    this.validationResult.set(null);
    this.importResult.set(null);
  }

  onTickerChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.tickerFilter.set(input.value);
  }

  /* ---- API calls ---- */

  validate(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.loading.set(true);
    this.validationResult.set(null);
    this.importResult.set(null);

    this.importService.validateXml(file).subscribe({
      next: (result) => {
        this.validationResult.set(result);
        this.loading.set(false);
      },
      error: () => {
        this.validationResult.set({ valid: false, message: 'Validation request failed', timestamp: new Date().toISOString() });
        this.loading.set(false);
      },
    });
  }

  importXml(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.loading.set(true);
    this.importResult.set(null);
    this.validationResult.set(null);

    const ticker = this.tickerFilter() || undefined;

    this.importService.importXml(file, ticker).subscribe({
      next: (result) => {
        this.importResult.set(result);
        this.loading.set(false);
      },
      error: () => {
        this.importResult.set({ importedCount: 0, status: 'ERROR', timestamp: new Date().toISOString(), reports: [] });
        this.loading.set(false);
      },
    });
  }

  /* ---- Sample XML ---- */

  toggleSample(): void {
    const current = this.showSample();
    if (!current && !this.sampleXml()) {
      this.loadSampleXml();
    }
    this.showSample.set(!current);
  }

  downloadSample(): void {
    if (this.sampleXml()) {
      this.triggerDownload(this.sampleXml());
      return;
    }

    this.importService.getSampleXml().subscribe({
      next: (xml) => {
        this.sampleXml.set(xml);
        this.triggerDownload(xml);
      },
    });
  }

  /* ---- Helpers ---- */

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  private selectFile(file: File): void {
    this.selectedFile.set(file);
    this.validationResult.set(null);
    this.importResult.set(null);
  }

  private loadSampleXml(): void {
    this.importService.getSampleXml().subscribe({
      next: (xml) => this.sampleXml.set(xml),
    });
  }

  private triggerDownload(content: string): void {
    const blob = new Blob([content], { type: 'application/xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'sample-reports.xml';
    a.click();
    URL.revokeObjectURL(url);
  }
}
