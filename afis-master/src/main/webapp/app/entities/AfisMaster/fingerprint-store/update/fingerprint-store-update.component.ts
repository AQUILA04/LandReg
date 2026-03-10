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
import { FingerprintStoreService } from '../service/fingerprint-store.service';
import { IFingerprintStore } from '../fingerprint-store.model';
import { FingerprintStoreFormGroup, FingerprintStoreFormService } from './fingerprint-store-form.service';

@Component({
  standalone: true,
  selector: 'jhi-fingerprint-store-update',
  templateUrl: './fingerprint-store-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FingerprintStoreUpdateComponent implements OnInit {
  isSaving = false;
  fingerprintStore: IFingerprintStore | null = null;
  handTypeValues = Object.keys(HandType);
  fingerValues = Object.keys(Finger);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected fingerprintStoreService = inject(FingerprintStoreService);
  protected fingerprintStoreFormService = inject(FingerprintStoreFormService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FingerprintStoreFormGroup = this.fingerprintStoreFormService.createFingerprintStoreFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fingerprintStore }) => {
      this.fingerprintStore = fingerprintStore;
      if (fingerprintStore) {
        this.updateForm(fingerprintStore);
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
    const fingerprintStore = this.fingerprintStoreFormService.getFingerprintStore(this.editForm);
    if (fingerprintStore.id !== null) {
      this.subscribeToSaveResponse(this.fingerprintStoreService.update(fingerprintStore));
    } else {
      this.subscribeToSaveResponse(this.fingerprintStoreService.create(fingerprintStore));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFingerprintStore>>): void {
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

  protected updateForm(fingerprintStore: IFingerprintStore): void {
    this.fingerprintStore = fingerprintStore;
    this.fingerprintStoreFormService.resetForm(this.editForm, fingerprintStore);
  }
}
