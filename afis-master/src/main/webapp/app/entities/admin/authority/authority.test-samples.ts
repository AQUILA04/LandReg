import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'cfb5dfa3-afcb-4735-9a4f-12580c2fe85c',
};

export const sampleWithPartialData: IAuthority = {
  name: 'bea540a3-71ec-41fa-8816-58aa22886e5d',
};

export const sampleWithFullData: IAuthority = {
  name: 'c09789b0-ba7c-489b-9c3f-8769aaa56428',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
