import { IFingerprintStore, NewFingerprintStore } from './fingerprint-store.model';

export const sampleWithRequiredData: IFingerprintStore = {
  id: '7c16ec1d-d829-4a57-b589-7969cbfc2dff',
  rid: 'hé',
};

export const sampleWithPartialData: IFingerprintStore = {
  id: '0a8910c7-6e37-4d62-b18d-6850e592c7bb',
  rid: "à l'exception de",
  handType: 'LEFT',
  fingerprintImage: '../fake-data/blob/hipster.png',
  fingerprintImageContentType: 'unknown',
};

export const sampleWithFullData: IFingerprintStore = {
  id: 'd2010b96-0eab-4655-a60e-753ebe633818',
  rid: 'commis de cuisine détester triathlète',
  handType: 'RIGHT',
  fingerName: 'LITTLE',
  fingerprintImage: '../fake-data/blob/hipster.png',
  fingerprintImageContentType: 'unknown',
};

export const sampleWithNewData: NewFingerprintStore = {
  rid: 'assumer exagérer',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
