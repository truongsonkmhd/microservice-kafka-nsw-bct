import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, tap } from 'rxjs';

import { HoSoStatusEvent } from '../../core/models/ho-so-status-event.model';
import { nswStatusLabel } from '../../core/models/nsw-workflow.model';
import { NswWebSocketService } from '../../core/services/nsw-websocket.service';

@Injectable({ providedIn: 'root' })
export class HoSoLiveTrackerService {
  private readonly nswWs = inject(NswWebSocketService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly terminalNotified = new Set<string>();

  watch(maSoHoSo: string, options?: { notifyTerminal?: boolean }): Observable<HoSoStatusEvent> {
    const notifyTerminal = options?.notifyTerminal ?? true;

    return this.nswWs.watchHoSo(maSoHoSo).pipe(
      tap((event) => {
        if (!notifyTerminal) {
          return;
        }
        this.maybeNotifyTerminal(maSoHoSo, event);
      }),
    );
  }

  resetNotifications(maSoHoSo?: string): void {
    if (maSoHoSo) {
      this.terminalNotified.delete(`${maSoHoSo}:DA_PHE_DUYET`);
      this.terminalNotified.delete(`${maSoHoSo}:TU_CHOI`);
      return;
    }
    this.terminalNotified.clear();
  }

  private maybeNotifyTerminal(maSoHoSo: string, event: HoSoStatusEvent): void {
    const status = event.businessStatus;
    if (status !== 'DA_PHE_DUYET' && status !== 'TU_CHOI') {
      return;
    }

    const key = `${maSoHoSo}:${status}`;
    if (this.terminalNotified.has(key)) {
      return;
    }
    this.terminalNotified.add(key);

    if (status === 'DA_PHE_DUYET') {
      this.snackBar.open(
        `Hồ sơ ${maSoHoSo} đã được phê duyệt. Bạn có thể tiếp tục thủ tục hải quan.`,
        'Đóng',
        { duration: 8000, panelClass: ['snack-success'] },
      );
      return;
    }

    this.snackBar.open(
      `Hồ sơ ${maSoHoSo} bị từ chối. Vui lòng xem kết quả chi tiết.`,
      'Đóng',
      { duration: 8000, panelClass: ['snack-warn'] },
    );
  }
}
