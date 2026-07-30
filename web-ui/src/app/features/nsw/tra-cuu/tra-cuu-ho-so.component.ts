import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { NswApiService } from '../../../core/services/nsw-api.service';
import { HoSoTraCuuResult } from '../../../core/models/ho-so.model';
import { HoSoTrackerComponent } from '../../../shared/components/ho-so-tracker/ho-so-tracker.component';
import { HoSoLiveTrackerService } from '../../../shared/services/ho-so-live-tracker.service';

@Component({
  selector: 'app-tra-cuu-ho-so',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    HoSoTrackerComponent,
  ],
  templateUrl: './tra-cuu-ho-so.component.html',
  styleUrl: './tra-cuu-ho-so.component.scss',
})
export class TraCuuHoSoComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly nswApi = inject(NswApiService);
  private readonly liveTracker = inject(HoSoLiveTrackerService);
  private readonly route = inject(ActivatedRoute);

  private wsSub: Subscription | null = null;

  readonly form = this.fb.nonNullable.group({
    maSoHoSo: ['', Validators.required],
  });

  loading = false;
  result: HoSoTraCuuResult | null = null;
  errorMessage = '';
  wsConnected = false;

  ngOnInit(): void {
    const maSoHoSo = this.route.snapshot.queryParamMap.get('maSoHoSo');
    if (maSoHoSo) {
      this.form.patchValue({ maSoHoSo });
      this.search();
    }
  }

  ngOnDestroy(): void {
    this.stopWatching();
  }

  search(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.result = null;
    this.stopWatching();

    const trimmed = this.form.getRawValue().maSoHoSo.trim();

    this.nswApi.traCuuHoSo(trimmed).subscribe({
      next: (res) => {
        this.loading = false;
        this.result = res.data;
        this.startWatching(trimmed);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage =
          err.status === 404
            ? 'Không tìm thấy hồ sơ với mã đã nhập'
            : err.error?.message ?? err.message ?? 'Tra cứu thất bại';
      },
    });
  }

  private startWatching(maSoHoSo: string): void {
    this.liveTracker.resetNotifications(maSoHoSo);
    this.wsSub = this.liveTracker.watch(maSoHoSo).subscribe({
      next: (event) => {
        this.wsConnected = true;
        if (!this.result) {
          return;
        }
        this.result = {
          ...this.result,
          businessStatus: event.businessStatus,
          ketQua: event.ketQua ?? this.result.ketQua,
          lastModifiedDate: event.timestamp,
        };
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
