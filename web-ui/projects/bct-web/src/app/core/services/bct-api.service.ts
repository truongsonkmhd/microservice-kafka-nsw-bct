import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { BusinessStatus } from '../models/business-status.model';
import {
  DuyetRequest,
  HoSoDetail,
  HoSoListItem,
  TuChoiRequest,
} from '../models/ho-so.model';

@Injectable({ providedIn: 'root' })
export class BctApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  listHoSo(status: BusinessStatus = 'CHO_XU_LY'): Observable<ApiResponse<HoSoListItem[]>> {
    const params = new HttpParams().set('status', status);
    return this.http.get<ApiResponse<HoSoListItem[]>>(
      `${this.baseUrl}/thu-tuc-1/ho-so`,
      { params },
    );
  }

  getHoSoDetail(maSoHoSo: string): Observable<ApiResponse<HoSoDetail>> {
    return this.http.get<ApiResponse<HoSoDetail>>(
      `${this.baseUrl}/thu-tuc-1/ho-so/${encodeURIComponent(maSoHoSo)}`,
    );
  }

  duyetHoSo(maSoHoSo: string, request: DuyetRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.baseUrl}/thu-tuc-1/ho-so/${encodeURIComponent(maSoHoSo)}/duyet`,
      request,
    );
  }

  tuChoiHoSo(maSoHoSo: string, request: TuChoiRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.baseUrl}/thu-tuc-1/ho-so/${encodeURIComponent(maSoHoSo)}/tu-choi`,
      request,
    );
  }
}
