import { BusinessStatus } from './business-status.model';

export interface GuiHoSoRequest {
  maSoHoSo?: string | null;
  tenNguoiGui: string;
}

export interface GuiHoSoSubmitResult {
  maSoHoSo: string;
  status: string;
}

export interface HoSoTraCuuResult {
  maSoHoSo: string;
  businessStatus: BusinessStatus;
  ketQua?: string;
  createdDate?: string;
  lastModifiedDate?: string;
}

export interface HoSoListItem {
  maSoHoSo: string;
  tenNguoiGui: string;
}

export interface HoSoDetail {
  maSoHoSo: string;
  tenNguoiGui: string;
  businessStatus: BusinessStatus;
  taiLieuDinhKem?: string[];
  bucketName?: string;
}

export interface DuyetRequest {
  tenNguoiXuLy: string;
  ketQua: string;
}

export interface TuChoiRequest {
  tenNguoiXuLy: string;
  lyDo: string;
}
