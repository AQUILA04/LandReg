import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 'd500d7a5-fe7a-4357-8cec-58075ae1fdfc',
  login: 'c_iR@M',
};

export const sampleWithPartialData: IUser = {
  id: '4142eac2-3e87-4745-b7ad-43f56391b707',
  login: 'D@0Ip\\zFjk\\J2129\\O6vyhjS\\lW3\\{ZTi',
};

export const sampleWithFullData: IUser = {
  id: 'abeb4520-ac6f-428a-92cc-b0e410f84208',
  login: '6qFN',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
