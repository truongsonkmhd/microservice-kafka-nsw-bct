import { BusinessStatus } from './business-status.model';

export type NswWorkflowStepId =
  | 'TIEP_NHAN'
  | 'CHUYEN_BCT'
  | 'CHO_PHE_DUYET'
  | 'KET_QUA';

export interface NswWorkflowStep {
  id: NswWorkflowStepId;
  order: number;
  label: string;
  hint: string;
  icon: string;
}

/** 4 bước doanh nghiệp nhìn thấy — map từ sơ đồ tuần tự NSW */
export const NSW_WORKFLOW_STEPS: NswWorkflowStep[] = [
  {
    id: 'TIEP_NHAN',
    order: 0,
    label: 'Đã tiếp nhận',
    hint: 'NSW đã lưu hồ sơ và tệp đính kèm của bạn.',
    icon: 'inbox',
  },
  {
    id: 'CHUYEN_BCT',
    order: 1,
    label: 'Chuyển sang BCT',
    hint: 'Hệ thống đang gửi hồ sơ sang Bộ Công Thương qua kênh liên thông.',
    icon: 'sync_alt',
  },
  {
    id: 'CHO_PHE_DUYET',
    order: 2,
    label: 'Chờ phê duyệt',
    hint: 'BCT đang thẩm định hồ sơ. Bạn sẽ được thông báo khi có kết quả.',
    icon: 'hourglass_top',
  },
  {
    id: 'KET_QUA',
    order: 3,
    label: 'Kết quả xử lý',
    hint: 'NSW đã nhận kết quả từ BCT và cập nhật trạng thái hồ sơ.',
    icon: 'task_alt',
  },
];

export const NSW_STATUS_LABELS: Partial<Record<BusinessStatus, string>> = {
  KHOI_TAO: 'Đã tiếp nhận',
  CHO_PHE_DUYET: 'Chờ phê duyệt BCT',
  DA_PHE_DUYET: 'Đã phê duyệt',
  TU_CHOI: 'Bị từ chối',
};

export const NSW_STATUS_HINTS: Partial<Record<BusinessStatus, string>> = {
  KHOI_TAO: 'Hồ sơ vừa được tạo — đang chuyển sang BCT.',
  CHO_PHE_DUYET: 'Hồ sơ đã tới BCT — đang chờ cán bộ ra quyết định.',
  DA_PHE_DUYET: 'BCT đã phê duyệt — bạn có thể tiếp tục thủ tục hải quan.',
  TU_CHOI: 'BCT đã từ chối hồ sơ — xem chi tiết kết quả bên dưới.',
};

export interface NswWorkflowState {
  currentStepIndex: number;
  completedStepIndices: number[];
  isTerminal: boolean;
  isApproved: boolean;
  isRejected: boolean;
}

export function resolveNswWorkflowState(
  status: BusinessStatus | null | undefined,
): NswWorkflowState {
  switch (status) {
    case 'DA_PHE_DUYET':
      return {
        currentStepIndex: 3,
        completedStepIndices: [0, 1, 2, 3],
        isTerminal: true,
        isApproved: true,
        isRejected: false,
      };
    case 'TU_CHOI':
      return {
        currentStepIndex: 3,
        completedStepIndices: [0, 1, 2, 3],
        isTerminal: true,
        isApproved: false,
        isRejected: true,
      };
    case 'CHO_PHE_DUYET':
      return {
        currentStepIndex: 2,
        completedStepIndices: [0, 1],
        isTerminal: false,
        isApproved: false,
        isRejected: false,
      };
    case 'KHOI_TAO':
    default:
      return {
        currentStepIndex: 1,
        completedStepIndices: [0],
        isTerminal: false,
        isApproved: false,
        isRejected: false,
      };
  }
}

export function nswStatusLabel(status: BusinessStatus | null | undefined): string {
  if (!status) {
    return '—';
  }
  return NSW_STATUS_LABELS[status] ?? status;
}
