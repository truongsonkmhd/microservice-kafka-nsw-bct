import { Pipe, PipeTransform } from '@angular/core';

import {
  BUSINESS_STATUS_LABELS,
  BusinessStatus,
} from '../../core/models/business-status.model';

@Pipe({ name: 'businessStatusLabel', standalone: true })
export class BusinessStatusLabelPipe implements PipeTransform {
  transform(value: BusinessStatus | null | undefined): string {
    if (!value) {
      return '—';
    }
    return BUSINESS_STATUS_LABELS[value] ?? value;
  }
}
