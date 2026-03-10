import { HandType } from 'app/entities/enumerations/hand-type.model';
import { Finger } from 'app/entities/enumerations/finger.model';

export interface IFingerprintStore {
  id: string;
  rid?: string | null;
  handType?: keyof typeof HandType | null;
  fingerName?: keyof typeof Finger | null;
  fingerprintImage?: string | null;
  fingerprintImageContentType?: string | null;
}

export type NewFingerprintStore = Omit<IFingerprintStore, 'id'> & { id: null };
