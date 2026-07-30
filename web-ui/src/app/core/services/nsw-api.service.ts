import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
  GuiHoSoRequest,
  GuiHoSoSubmitResult,
  HoSoTraCuuResult,
} from '../models/ho-so.model';

@Injectable({ providedIn: 'root' })
export class NswApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  guiHoSo(thongTin: GuiHoSoRequest, files: File[]): Observable<ApiResponse<GuiHoSoSubmitResult>> {
    const formData = new FormData();
    formData.append(
      'thongTin',
      new Blob([JSON.stringify(thongTin)], { type: 'application/json' }),
    );
    files.forEach((file) => formData.append('tepDinhKem', file, file.name));

    return this.http.post<ApiResponse<GuiHoSoSubmitResult>>(
      `${this.baseUrl}/thu-tuc-1/gui-ho-so`,
      formData,
    );
  }

  traCuuHoSo(maSoHoSo: string): Observable<ApiResponse<HoSoTraCuuResult>> {
    return this.http.get<ApiResponse<HoSoTraCuuResult>>(
      `${this.baseUrl}/thu-tuc-1/ho-so/${encodeURIComponent(maSoHoSo)}`,
    );
  }
}
