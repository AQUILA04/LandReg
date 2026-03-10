import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IProcessingFingerprint } from '../processing-fingerprint.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../processing-fingerprint.test-samples';

import { ProcessingFingerprintService } from './processing-fingerprint.service';

const requireRestSample: IProcessingFingerprint = {
  ...sampleWithRequiredData,
};

describe('ProcessingFingerprint Service', () => {
  let service: ProcessingFingerprintService;
  let httpMock: HttpTestingController;
  let expectedResult: IProcessingFingerprint | IProcessingFingerprint[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProcessingFingerprintService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ProcessingFingerprint', () => {
      const processingFingerprint = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(processingFingerprint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProcessingFingerprint', () => {
      const processingFingerprint = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(processingFingerprint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProcessingFingerprint', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProcessingFingerprint', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProcessingFingerprint', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProcessingFingerprintToCollectionIfMissing', () => {
      it('should add a ProcessingFingerprint to an empty array', () => {
        const processingFingerprint: IProcessingFingerprint = sampleWithRequiredData;
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing([], processingFingerprint);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(processingFingerprint);
      });

      it('should not add a ProcessingFingerprint to an array that contains it', () => {
        const processingFingerprint: IProcessingFingerprint = sampleWithRequiredData;
        const processingFingerprintCollection: IProcessingFingerprint[] = [
          {
            ...processingFingerprint,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing(processingFingerprintCollection, processingFingerprint);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProcessingFingerprint to an array that doesn't contain it", () => {
        const processingFingerprint: IProcessingFingerprint = sampleWithRequiredData;
        const processingFingerprintCollection: IProcessingFingerprint[] = [sampleWithPartialData];
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing(processingFingerprintCollection, processingFingerprint);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(processingFingerprint);
      });

      it('should add only unique ProcessingFingerprint to an array', () => {
        const processingFingerprintArray: IProcessingFingerprint[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const processingFingerprintCollection: IProcessingFingerprint[] = [sampleWithRequiredData];
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing(
          processingFingerprintCollection,
          ...processingFingerprintArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const processingFingerprint: IProcessingFingerprint = sampleWithRequiredData;
        const processingFingerprint2: IProcessingFingerprint = sampleWithPartialData;
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing([], processingFingerprint, processingFingerprint2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(processingFingerprint);
        expect(expectedResult).toContain(processingFingerprint2);
      });

      it('should accept null and undefined values', () => {
        const processingFingerprint: IProcessingFingerprint = sampleWithRequiredData;
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing([], null, processingFingerprint, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(processingFingerprint);
      });

      it('should return initial array if no ProcessingFingerprint is added', () => {
        const processingFingerprintCollection: IProcessingFingerprint[] = [sampleWithRequiredData];
        expectedResult = service.addProcessingFingerprintToCollectionIfMissing(processingFingerprintCollection, undefined, null);
        expect(expectedResult).toEqual(processingFingerprintCollection);
      });
    });

    describe('compareProcessingFingerprint', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProcessingFingerprint(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareProcessingFingerprint(entity1, entity2);
        const compareResult2 = service.compareProcessingFingerprint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareProcessingFingerprint(entity1, entity2);
        const compareResult2 = service.compareProcessingFingerprint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareProcessingFingerprint(entity1, entity2);
        const compareResult2 = service.compareProcessingFingerprint(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
