import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ProcessingFingerprintService } from '../service/processing-fingerprint.service';
import { IProcessingFingerprint } from '../processing-fingerprint.model';
import { ProcessingFingerprintFormService } from './processing-fingerprint-form.service';

import { ProcessingFingerprintUpdateComponent } from './processing-fingerprint-update.component';

describe('ProcessingFingerprint Management Update Component', () => {
  let comp: ProcessingFingerprintUpdateComponent;
  let fixture: ComponentFixture<ProcessingFingerprintUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let processingFingerprintFormService: ProcessingFingerprintFormService;
  let processingFingerprintService: ProcessingFingerprintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProcessingFingerprintUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProcessingFingerprintUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProcessingFingerprintUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    processingFingerprintFormService = TestBed.inject(ProcessingFingerprintFormService);
    processingFingerprintService = TestBed.inject(ProcessingFingerprintService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const processingFingerprint: IProcessingFingerprint = { id: 'CBA' };

      activatedRoute.data = of({ processingFingerprint });
      comp.ngOnInit();

      expect(comp.processingFingerprint).toEqual(processingFingerprint);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProcessingFingerprint>>();
      const processingFingerprint = { id: 'ABC' };
      jest.spyOn(processingFingerprintFormService, 'getProcessingFingerprint').mockReturnValue(processingFingerprint);
      jest.spyOn(processingFingerprintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processingFingerprint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: processingFingerprint }));
      saveSubject.complete();

      // THEN
      expect(processingFingerprintFormService.getProcessingFingerprint).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(processingFingerprintService.update).toHaveBeenCalledWith(expect.objectContaining(processingFingerprint));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProcessingFingerprint>>();
      const processingFingerprint = { id: 'ABC' };
      jest.spyOn(processingFingerprintFormService, 'getProcessingFingerprint').mockReturnValue({ id: null });
      jest.spyOn(processingFingerprintService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processingFingerprint: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: processingFingerprint }));
      saveSubject.complete();

      // THEN
      expect(processingFingerprintFormService.getProcessingFingerprint).toHaveBeenCalled();
      expect(processingFingerprintService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProcessingFingerprint>>();
      const processingFingerprint = { id: 'ABC' };
      jest.spyOn(processingFingerprintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processingFingerprint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(processingFingerprintService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
