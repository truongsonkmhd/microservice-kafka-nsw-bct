import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { BctApiService } from '../../core/services/bct-api.service';
import { BctWebSocketService } from '../../core/services/bct-websocket.service';
import {
  BCT_BUSINESS_STATUS_HINTS,
  BCT_BUSINESS_STATUS_OPTIONS,
  BctBusinessStatus,
  BUSINESS_STATUS_LABELS,
} from '../../core/models/business-status.model';
import { HoSoListItem } from '../../core/models/ho-so.model';

@Component({
  selector: 'app-ho-so-list',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './ho-so-list.component.html',
  styleUrl: './ho-so-list.component.scss',
})
export class HoSoListComponent implements OnInit, OnDestroy {
  private readonly bctApi = inject(BctApiService);
  private readonly bctWs = inject(BctWebSocketService);

  private wsSub: Subscription | null = null;
  private statusSub: Subscription | null = null;

  readonly statusControl = new FormControl<BctBusinessStatus>('CHO_XU_LY', { nonNullable: true });
  readonly statusOptions = BCT_BUSINESS_STATUS_OPTIONS;
  readonly statusLabels = BUSINESS_STATUS_LABELS;
  readonly statusHints = BCT_BUSINESS_STATUS_HINTS;

  get selectedStatusHint(): string {
    return this.statusHints[this.statusControl.value];
  }

  displayedColumns = ['maSoHoSo', 'tenNguoiGui', 'actions'];
  items: HoSoListItem[] = [];
  loading = false;
  errorMessage = '';
  wsConnected = false;
  lastLiveAt: string | null = null;

  ngOnInit(): void {
    this.loadList();
    this.statusSub = this.statusControl.valueChanges.subscribe(() => {
      this.loadList();
      this.connectWebSocket();
    });
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.statusSub?.unsubscribe();
  }

  loadList(): void {
    this.loading = true;
    this.errorMessage = '';

    this.bctApi.listHoSo(this.statusControl.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.items = res.data ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = err.error?.message ?? err.message ?? 'Không tải được danh sách';
      },
    });
  }

  private connectWebSocket(): void {
    this.wsSub?.unsubscribe();
    this.wsConnected = false;

    this.wsSub = this.bctWs.watchList(this.statusControl.value).subscribe({
      next: (event) => {
        this.wsConnected = true;
        this.lastLiveAt = event.timestamp;

        if (event.eventType === 'LIST_ITEM_ADDED' && event.maSoHoSo && event.tenNguoiGui) {
          const exists = this.items.some((item) => item.maSoHoSo === event.maSoHoSo);
          if (!exists) {
            this.items = [
              { maSoHoSo: event.maSoHoSo, tenNguoiGui: event.tenNguoiGui },
              ...this.items,
            ];
          }
        }

        if (event.eventType === 'LIST_ITEM_REMOVED' && event.maSoHoSo) {
          this.items = this.items.filter((item) => item.maSoHoSo !== event.maSoHoSo);
        }
      },
      error: () => {
        this.wsConnected = false;
      },
    });
  }
}
