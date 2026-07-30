import { DatePipe } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { BusinessStatus } from '../../../core/models/business-status.model';
import {
  NSW_WORKFLOW_STEPS,
  nswStatusLabel,
  NSW_STATUS_HINTS,
  resolveNswWorkflowState,
} from '../../../core/models/nsw-workflow.model';

@Component({
  selector: 'app-ho-so-tracker',
  standalone: true,
  imports: [DatePipe, MatCardModule, MatButtonModule, MatIconModule, MatSnackBarModule],
  templateUrl: './ho-so-tracker.component.html',
  styleUrl: './ho-so-tracker.component.scss',
})
export class HoSoTrackerComponent {
  private readonly snackBar = inject(MatSnackBar);

  @Input({ required: true }) maSoHoSo!: string;
  @Input() businessStatus: BusinessStatus | null = null;
  @Input() ketQua: string | null = null;
  @Input() lastUpdated: string | null = null;
  @Input() wsConnected = false;

  readonly steps = NSW_WORKFLOW_STEPS;

  get workflow() {
    return resolveNswWorkflowState(this.businessStatus ?? undefined);
  }

  get statusLabel(): string {
    return nswStatusLabel(this.businessStatus ?? undefined);
  }

  get statusHint(): string {
    if (!this.businessStatus) {
      return '';
    }
    return NSW_STATUS_HINTS[this.businessStatus] ?? '';
  }

  stepState(index: number): 'done' | 'active' | 'pending' {
    if (this.workflow.completedStepIndices.includes(index)) {
      return 'done';
    }
    if (index === this.workflow.currentStepIndex) {
      return 'active';
    }
    return 'pending';
  }

  async copyMaSoHoSo(): Promise<void> {
    try {
      await navigator.clipboard.writeText(this.maSoHoSo);
      this.snackBar.open('Đã sao chép mã hồ sơ', 'Đóng', { duration: 2500 });
    } catch {
      this.snackBar.open('Không thể sao chép mã hồ sơ', 'Đóng', { duration: 3000 });
    }
  }
}
