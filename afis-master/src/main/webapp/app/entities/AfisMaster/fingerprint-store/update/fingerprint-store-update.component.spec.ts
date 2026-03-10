import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { FingerprintStoreService } from '../service/fingerprint-store.service';
import { IFingerprintStore } from '../fingerprint-store.model';
import { FingerprintStoreFormService } from './fingerprint-store-form.service';

import { FingerprintStoreUpdateComponent } from './fingerprint-store-update.component';

describe('FingerprintStore Management Update Component', () => {
  let comp: FingerprintStoreUpdateComponent;
  let fixture: ComponentFixture<FingerprintStoreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fingerprintStoreFormService: FingerprintStoreFormService;
  let fingerprintStoreService: FingerprintStoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FingerprintStoreUpdateComponent],
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
      .overrideTemplate(FingerprintStoreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FingerprintStoreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fingerprintStoreFormService = TestBed.inject(FingerprintStoreFormService);
    fingerprintStoreService = TestBed.inject(FingerprintStoreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const fingerprintStore: IFingerprintStore = { id: 'CBA' };

      activatedRoute.data = of({ fingerprintStore });
      comp.ngOnInit();

      expect(comp.fingerprintStore).toEqual(fingerprintStore);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFingerprintStore>>();
      const fingerprintStore = { id: 'ABC' };
      jest.spyOn(fingerprintStoreFormService, 'getFingerprintStore').mockReturnValue(fingerprintStore);
      jest.spyOn(fingerprintStoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fingerprintStore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fingerprintStore }));
      saveSubject.complete();

      // THEN
      expect(fingerprintStoreFormService.getFingerprintStore).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fingerprintStoreService.update).toHaveBeenCalledWith(expect.objectContaining(fingerprintStore));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFingerprintStore>>();
      const fingerprintStore = { id: 'ABC' };
      jest.spyOn(fingerprintStoreFormService, 'getFingerprintStore').mockReturnValue({ id: null });
      jest.spyOn(fingerprintStoreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fingerprintStore: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fingerprintStore }));
      saveSubject.complete();

      // THEN
      expect(fingerprintStoreFormService.getFingerprintStore).toHaveBeenCalled();
      expect(fingerprintStoreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFingerprintStore>>();
      const fingerprintStore = { id: 'ABC' };
      jest.spyOn(fingerprintStoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fingerprintStore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fingerprintStoreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
