import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { DuyetRequest } from '../../core/models/ho-so.model';

@Component({
  selector: 'app-duyet-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>Phê duyệt hồ sơ</h2>
    <form [formGroup]="form" (ngSubmit)="submit()">
      <div mat-dialog-content class="dialog-content">
        <mat-form-field appearance="outline">
          <mat-label>Tên cán bộ xử lý</mat-label>
          <input matInput formControlName="tenNguoiXuLy" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Kết quả</mat-label>
          <textarea matInput rows="3" formControlName="ketQua"></textarea>
        </mat-form-field>
      </div>
      <div mat-dialog-actions align="end">
        <button mat-button type="button" (click)="dialogRef.close()">Hủy</button>
        <button mat-flat-button color="primary" type="submit">Xác nhận duyệt</button>
      </div>
    </form>
  `,
  styles: `
    .dialog-content {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      min-width: 360px;
    }
  `,
})
export class DuyetDialogComponent {
  readonly dialogRef = inject(MatDialogRef<DuyetDialogComponent>);
  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    tenNguoiXuLy: ['', Validators.required],
    ketQua: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue() as DuyetRequest);
  }
}
