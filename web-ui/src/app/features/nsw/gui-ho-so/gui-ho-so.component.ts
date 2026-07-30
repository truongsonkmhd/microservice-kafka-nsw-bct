import { Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { BusinessStatus } from '../../../core/models/business-status.model';
import { NswApiService } from '../../../core/services/nsw-api.service';
import { GuiHoSoSubmitResult } from '../../../core/models/ho-so.model';
import { HoSoTrackerComponent } from '../../../shared/components/ho-so-tracker/ho-so-tracker.component';
import { HoSoLiveTrackerService } from '../../../shared/services/ho-so-live-tracker.service';

@Component({
  selector: 'app-gui-ho-so',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatSnackBarModule,
    HoSoTrackerComponent,
  ],
  templateUrl: './gui-ho-so.component.html',
  styleUrl: './gui-ho-so.component.scss',
})
export class GuiHoSoComponent implements OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly nswApi = inject(NswApiService);
  private readonly liveTracker = inject(HoSoLiveTrackerService);

  private wsSub: Subscription | null = null;

  readonly form = this.fb.nonNullable.group({
    tenNguoiGui: ['', [Validators.required, Validators.maxLength(200)]],
  });

  selectedFiles: File[] = [];
  loading = false;
  submitResult: GuiHoSoSubmitResult | null = null;
  errorMessage = '';
  businessStatus: BusinessStatus | null = null;
  ketQua: string | null = null;
  lastUpdated: string | null = null;
  wsConnected = false;

  ngOnDestroy(): void {
    this.stopWatching();
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFiles = input.files ? Array.from(input.files) : [];
  }

  removeFile(index: number): void {
    this.selectedFiles = this.selectedFiles.filter((_, i) => i !== index);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.submitResult = null;
    this.businessStatus = null;
    this.ketQua = null;
    this.lastUpdated = null;
    this.stopWatching();

    const { tenNguoiGui } = this.form.getRawValue();
    const files = [...this.selectedFiles];

    this.nswApi.guiHoSo({ tenNguoiGui, maSoHoSo: null }, files).subscribe({
      next: (res) => {
        this.loading = false;
        this.submitResult = res.data;
        this.businessStatus = 'KHOI_TAO';
        this.form.reset();
        this.selectedFiles = [];
        this.startWatching(res.data.maSoHoSo);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = err.error?.message ?? err.message ?? 'Không thể nộp hồ sơ';
      },
    });
  }

  private startWatching(maSoHoSo: string): void {
    this.liveTracker.resetNotifications(maSoHoSo);
    this.wsSub = this.liveTracker.watch(maSoHoSo).subscribe({
      next: (event) => {
        this.wsConnected = true;
        this.businessStatus = event.businessStatus;
        this.ketQua = event.ketQua ?? this.ketQua;
        this.lastUpdated = event.timestamp;
      },
      error: () => {
        this.wsConnected = false;
      },
    });
  }

  private stopWatching(): void {
    this.wsSub?.unsubscribe();
    this.wsSub = null;
    this.wsConnected = false;
  }
}
