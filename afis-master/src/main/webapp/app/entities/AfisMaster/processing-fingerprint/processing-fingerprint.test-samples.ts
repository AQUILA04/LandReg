import { IProcessingFingerprint, NewProcessingFingerprint } from './processing-fingerprint.model';

export const sampleWithRequiredData: IProcessingFingerprint = {
  id: 'dd05cf87-72eb-44f0-85e6-0f1564fc83f0',
  rid: 'triathlète',
};

export const sampleWithPartialData: IProcessingFingerprint = {
  id: '55609fc8-bf99-4f05-a95f-a15c81dcc503',
  rid: 'vis-à-vie de économiser',
  fingerName: 'RING',
};

export const sampleWithFullData: IProcessingFingerprint = {
  id: '9473cc88-1c64-4200-b8e7-4cc5ed42d643',
  rid: 'lentement appuyer différencier',
  handType: 'RIGHT',
  fingerName: 'THUMB',
  fingerprintImage: '../fake-data/blob/hipster.png',
  fingerprintImageContentType: 'unknown',
};

export const sampleWithNewData: NewProcessingFingerprint = {
  rid: 'carrément',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
