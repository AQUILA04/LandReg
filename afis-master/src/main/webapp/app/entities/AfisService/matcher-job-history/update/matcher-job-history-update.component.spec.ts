import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { MatcherJobHistoryService } from '../service/matcher-job-history.service';
import { IMatcherJobHistory } from '../matcher-job-history.model';
import { MatcherJobHistoryFormService } from './matcher-job-history-form.service';

import { MatcherJobHistoryUpdateComponent } from './matcher-job-history-update.component';

describe('MatcherJobHistory Management Update Component', () => {
  let comp: MatcherJobHistoryUpdateComponent;
  let fixture: ComponentFixture<MatcherJobHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let matcherJobHistoryFormService: MatcherJobHistoryFormService;
  let matcherJobHistoryService: MatcherJobHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatcherJobHistoryUpdateComponent],
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
      .overrideTemplate(MatcherJobHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MatcherJobHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    matcherJobHistoryFormService = TestBed.inject(MatcherJobHistoryFormService);
    matcherJobHistoryService = TestBed.inject(MatcherJobHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const matcherJobHistory: IMatcherJobHistory = { id: 'CBA' };

      activatedRoute.data = of({ matcherJobHistory });
      comp.ngOnInit();

      expect(comp.matcherJobHistory).toEqual(matcherJobHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatcherJobHistory>>();
      const matcherJobHistory = { id: 'ABC' };
      jest.spyOn(matcherJobHistoryFormService, 'getMatcherJobHistory').mockReturnValue(matcherJobHistory);
      jest.spyOn(matcherJobHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ matcherJobHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: matcherJobHistory }));
      saveSubject.complete();

      // THEN
      expect(matcherJobHistoryFormService.getMatcherJobHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(matcherJobHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(matcherJobHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatcherJobHistory>>();
      const matcherJobHistory = { id: 'ABC' };
      jest.spyOn(matcherJobHistoryFormService, 'getMatcherJobHistory').mockReturnValue({ id: null });
      jest.spyOn(matcherJobHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ matcherJobHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: matcherJobHistory }));
      saveSubject.complete();

      // THEN
      expect(matcherJobHistoryFormService.getMatcherJobHistory).toHaveBeenCalled();
      expect(matcherJobHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMatcherJobHistory>>();
      const matcherJobHistory = { id: 'ABC' };
      jest.spyOn(matcherJobHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ matcherJobHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(matcherJobHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
