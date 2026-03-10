import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IFingerprintStore } from '../fingerprint-store.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../fingerprint-store.test-samples';

import { FingerprintStoreService } from './fingerprint-store.service';

const requireRestSample: IFingerprintStore = {
  ...sampleWithRequiredData,
};

describe('FingerprintStore Service', () => {
  let service: FingerprintStoreService;
  let httpMock: HttpTestingController;
  let expectedResult: IFingerprintStore | IFingerprintStore[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FingerprintStoreService);
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

    it('should create a FingerprintStore', () => {
      const fingerprintStore = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(fingerprintStore).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FingerprintStore', () => {
      const fingerprintStore = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(fingerprintStore).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FingerprintStore', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FingerprintStore', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FingerprintStore', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFingerprintStoreToCollectionIfMissing', () => {
      it('should add a FingerprintStore to an empty array', () => {
        const fingerprintStore: IFingerprintStore = sampleWithRequiredData;
        expectedResult = service.addFingerprintStoreToCollectionIfMissing([], fingerprintStore);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fingerprintStore);
      });

      it('should not add a FingerprintStore to an array that contains it', () => {
        const fingerprintStore: IFingerprintStore = sampleWithRequiredData;
        const fingerprintStoreCollection: IFingerprintStore[] = [
          {
            ...fingerprintStore,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFingerprintStoreToCollectionIfMissing(fingerprintStoreCollection, fingerprintStore);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FingerprintStore to an array that doesn't contain it", () => {
        const fingerprintStore: IFingerprintStore = sampleWithRequiredData;
        const fingerprintStoreCollection: IFingerprintStore[] = [sampleWithPartialData];
        expectedResult = service.addFingerprintStoreToCollectionIfMissing(fingerprintStoreCollection, fingerprintStore);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fingerprintStore);
      });

      it('should add only unique FingerprintStore to an array', () => {
        const fingerprintStoreArray: IFingerprintStore[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const fingerprintStoreCollection: IFingerprintStore[] = [sampleWithRequiredData];
        expectedResult = service.addFingerprintStoreToCollectionIfMissing(fingerprintStoreCollection, ...fingerprintStoreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const fingerprintStore: IFingerprintStore = sampleWithRequiredData;
        const fingerprintStore2: IFingerprintStore = sampleWithPartialData;
        expectedResult = service.addFingerprintStoreToCollectionIfMissing([], fingerprintStore, fingerprintStore2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(fingerprintStore);
        expect(expectedResult).toContain(fingerprintStore2);
      });

      it('should accept null and undefined values', () => {
        const fingerprintStore: IFingerprintStore = sampleWithRequiredData;
        expectedResult = service.addFingerprintStoreToCollectionIfMissing([], null, fingerprintStore, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(fingerprintStore);
      });

      it('should return initial array if no FingerprintStore is added', () => {
        const fingerprintStoreCollection: IFingerprintStore[] = [sampleWithRequiredData];
        expectedResult = service.addFingerprintStoreToCollectionIfMissing(fingerprintStoreCollection, undefined, null);
        expect(expectedResult).toEqual(fingerprintStoreCollection);
      });
    });

    describe('compareFingerprintStore', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFingerprintStore(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareFingerprintStore(entity1, entity2);
        const compareResult2 = service.compareFingerprintStore(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareFingerprintStore(entity1, entity2);
        const compareResult2 = service.compareFingerprintStore(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareFingerprintStore(entity1, entity2);
        const compareResult2 = service.compareFingerprintStore(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
