import { IMatcherJobHistory, NewMatcherJobHistory } from './matcher-job-history.model';

export const sampleWithRequiredData: IMatcherJobHistory = {
  id: '9ea8e4fb-4e80-488d-8791-6edf7e57e304',
};

export const sampleWithPartialData: IMatcherJobHistory = {
  id: 'a53289bd-652e-49ed-b83b-da0fafe37d3e',
  rid: 'désormais',
  producerCount: 23053,
  consumerReponseCount: 13543,
  highScore: 29052.81,
  foundMatch: false,
  matchedRID: 'avant de sans',
  status: 'PENDING',
};

export const sampleWithFullData: IMatcherJobHistory = {
  id: '56208c52-d7a5-4092-84ab-13560097e5db',
  rid: 'presser',
  producerCount: 3403,
  consumerReponseCount: 31521,
  highScore: 21102.02,
  foundMatch: true,
  matchedRID: "miam d'entre",
  status: 'PROCESSING',
};

export const sampleWithNewData: NewMatcherJobHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
