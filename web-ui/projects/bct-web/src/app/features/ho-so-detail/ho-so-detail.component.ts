import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

import { BctApiService } from '../../core/services/bct-api.service';
import { HoSoDetail } from '../../core/models/ho-so.model';
import { BusinessStatusLabelPipe } from '../../shared/pipes/business-status-label.pipe';
import { DuyetDialogComponent } from '../duyet-dialog/duyet-dialog.component';
import { TuChoiDialogComponent } from '../tu-choi-dialog/tu-choi-dialog.component';

@Component({
  selector: 'app-ho-so-detail',
  standalone: true,
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule,
    BusinessStatusLabelPipe,
  ],
  templateUrl: './ho-so-detail.component.html',
  styleUrl: './ho-so-detail.component.scss',
})
export class HoSoDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly bctApi = inject(BctApiService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  maSoHoSo = '';
  detail: HoSoDetail | null = null;
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.maSoHoSo = this.route.snapshot.paramMap.get('maSoHoSo') ?? '';
    this.loadDetail();
  }

  get canProcess(): boolean {
    return this.detail?.businessStatus === 'CHO_XU_LY';
  }

  loadDetail(): void {
    this.loading = true;
    this.errorMessage = '';

    this.bctApi.getHoSoDetail(this.maSoHoSo).subscribe({
      next: (res) => {
        this.loading = false;
        this.detail = res.data;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage =
          err.status === 404
            ? 'Hồ sơ không tồn tại'
            : err.error?.message ?? err.message ?? 'Không tải được chi tiết';
      },
    });
  }

  openDuyetDialog(): void {
    const ref = this.dialog.open(DuyetDialogComponent, { width: '480px' });
    ref.afterClosed().subscribe((payload) => {
      if (!payload) {
        return;
      }
      this.bctApi.duyetHoSo(this.maSoHoSo, payload).subscribe({
        next: () => {
          this.snackBar.open('Phê duyệt hồ sơ thành công', 'Đóng', { duration: 4000 });
          this.loadDetail();
        },
        error: (err: HttpErrorResponse) => {
          this.snackBar.open(err.error?.message ?? 'Phê duyệt thất bại', 'Đóng', {
            duration: 5000,
          });
        },
      });
    });
  }

  openTuChoiDialog(): void {
    const ref = this.dialog.open(TuChoiDialogComponent, { width: '480px' });
    ref.afterClosed().subscribe((payload) => {
      if (!payload) {
        return;
      }
      this.bctApi.tuChoiHoSo(this.maSoHoSo, payload).subscribe({
        next: () => {
          this.snackBar.open('Từ chối hồ sơ thành công', 'Đóng', { duration: 4000 });
          this.loadDetail();
        },
        error: (err: HttpErrorResponse) => {
          this.snackBar.open(err.error?.message ?? 'Từ chối thất bại', 'Đóng', {
            duration: 5000,
          });
        },
      });
    });
  }
}
