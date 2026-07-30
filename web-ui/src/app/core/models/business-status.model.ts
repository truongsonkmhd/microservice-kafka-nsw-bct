export type BusinessStatus =
  | 'KHOI_TAO'
  | 'CHO_XU_LY'
  | 'DA_XU_LY'
  | 'CHO_PHE_DUYET'
  | 'DA_PHE_DUYET'
  | 'DA_GUI_KET_QUA'
  | 'TU_CHOI'
  | 'DA_HUY';

export const BUSINESS_STATUS_LABELS: Record<BusinessStatus, string> = {
  KHOI_TAO: 'Khởi tạo',
  CHO_XU_LY: 'Chờ xử lý',
  DA_XU_LY: 'Đã xử lý',
  CHO_PHE_DUYET: 'Chờ phê duyệt',
  DA_PHE_DUYET: 'Đã phê duyệt',
  DA_GUI_KET_QUA: 'Đã gửi kết quả',
  TU_CHOI: 'Từ chối',
  DA_HUY: 'Đã hủy',
};

/** Tất cả status (dùng cho tra cứu NSW, pipe label, v.v.) */
export const BUSINESS_STATUS_OPTIONS: BusinessStatus[] = [
  'KHOI_TAO',
  'CHO_XU_LY',
  'DA_XU_LY',
  'CHO_PHE_DUYET',
  'DA_PHE_DUYET',
  'DA_GUI_KET_QUA',
  'TU_CHOI',
  'DA_HUY',
];

/** Chỉ các status workflow phía BCT — không bao gồm status chỉ tồn tại trên DB NSW */
export const BCT_BUSINESS_STATUS_OPTIONS = [
  'KHOI_TAO',
  'CHO_XU_LY',
  'DA_XU_LY',
  'DA_GUI_KET_QUA',
] as const;

export type BctBusinessStatus = (typeof BCT_BUSINESS_STATUS_OPTIONS)[number];

export const BCT_BUSINESS_STATUS_HINTS: Record<BctBusinessStatus, string> = {
  KHOI_TAO:
    'BCT gateway vừa nhận hồ sơ từ NSW; chờ Kafka chuyển sang hàng đợi cán bộ (vài giây).',
  CHO_XU_LY: 'Hồ sơ sẵn sàng — cán bộ mở chi tiết để phê duyệt hoặc từ chối.',
  DA_XU_LY:
    'Cán bộ vừa ra quyết định; hệ thống đang gửi SOAP TraLoi sang NSW (trạng thái chuyển tiếp).',
  DA_GUI_KET_QUA: 'Đã gửi kết quả về NSW — hồ sơ hoàn tất phía BCT.',
};
