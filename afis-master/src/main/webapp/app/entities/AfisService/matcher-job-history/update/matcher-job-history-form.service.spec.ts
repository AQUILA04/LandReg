import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../matcher-job-history.test-samples';

import { MatcherJobHistoryFormService } from './matcher-job-history-form.service';

describe('MatcherJobHistory Form Service', () => {
  let service: MatcherJobHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MatcherJobHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createMatcherJobHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rid: expect.any(Object),
            producerCount: expect.any(Object),
            consumerReponseCount: expect.any(Object),
            highScore: expect.any(Object),
            foundMatch: expect.any(Object),
            matchedRID: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing IMatcherJobHistory should create a new form with FormGroup', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rid: expect.any(Object),
            producerCount: expect.any(Object),
            consumerReponseCount: expect.any(Object),
            highScore: expect.any(Object),
            foundMatch: expect.any(Object),
            matchedRID: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getMatcherJobHistory', () => {
      it('should return NewMatcherJobHistory for default MatcherJobHistory initial value', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup(sampleWithNewData);

        const matcherJobHistory = service.getMatcherJobHistory(formGroup) as any;

        expect(matcherJobHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewMatcherJobHistory for empty MatcherJobHistory initial value', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup();

        const matcherJobHistory = service.getMatcherJobHistory(formGroup) as any;

        expect(matcherJobHistory).toMatchObject({});
      });

      it('should return IMatcherJobHistory', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup(sampleWithRequiredData);

        const matcherJobHistory = service.getMatcherJobHistory(formGroup) as any;

        expect(matcherJobHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMatcherJobHistory should not enable id FormControl', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMatcherJobHistory should disable id FormControl', () => {
        const formGroup = service.createMatcherJobHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
