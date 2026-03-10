import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { HandType } from 'app/entities/enumerations/hand-type.model';
import { Finger } from 'app/entities/enumerations/finger.model';
import { ProcessingFingerprintService } from '../service/processing-fingerprint.service';
import { IProcessingFingerprint } from '../processing-fingerprint.model';
import { ProcessingFingerprintFormGroup, ProcessingFingerprintFormService } from './processing-fingerprint-form.service';

@Component({
  standalone: true,
  selector: 'jhi-processing-fingerprint-update',
  templateUrl: './processing-fingerprint-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProcessingFingerprintUpdateComponent implements OnInit {
  isSaving = false;
  processingFingerprint: IProcessingFingerprint | null = null;
  handTypeValues = Object.keys(HandType);
  fingerValues = Object.keys(Finger);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected processingFingerprintService = inject(ProcessingFingerprintService);
  protected processingFingerprintFormService = inject(ProcessingFingerprintFormService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProcessingFingerprintFormGroup = this.processingFingerprintFormService.createProcessingFingerprintFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ processingFingerprint }) => {
      this.processingFingerprint = processingFingerprint;
      if (processingFingerprint) {
        this.updateForm(processingFingerprint);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('afisMasterApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const processingFingerprint = this.processingFingerprintFormService.getProcessingFingerprint(this.editForm);
    if (processingFingerprint.id !== null) {
      this.subscribeToSaveResponse(this.processingFingerprintService.update(processingFingerprint));
    } else {
      this.subscribeToSaveResponse(this.processingFingerprintService.create(processingFingerprint));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProcessingFingerprint>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(processingFingerprint: IProcessingFingerprint): void {
    this.processingFingerprint = processingFingerprint;
    this.processingFingerprintFormService.resetForm(this.editForm, processingFingerprint);
  }
}
