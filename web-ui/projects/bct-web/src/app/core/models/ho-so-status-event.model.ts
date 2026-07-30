import { BusinessStatus } from './business-status.model';

export type HoSoStatusEventType =
  | 'SNAPSHOT'
  | 'STATUS_CHANGED'
  | 'LIST_ITEM_ADDED'
  | 'LIST_ITEM_REMOVED';

export interface HoSoStatusEvent {
  maSoHoSo: string;
  tenNguoiGui?: string;
  businessStatus: BusinessStatus;
  ketQua?: string;
  eventType: HoSoStatusEventType;
  timestamp: string;
}
