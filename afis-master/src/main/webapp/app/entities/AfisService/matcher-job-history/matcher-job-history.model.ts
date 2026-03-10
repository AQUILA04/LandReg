import { MatchJobStatus } from 'app/entities/enumerations/match-job-status.model';

export interface IMatcherJobHistory {
  id: string;
  rid?: string | null;
  producerCount?: number | null;
  consumerReponseCount?: number | null;
  highScore?: number | null;
  foundMatch?: boolean | null;
  matchedRID?: string | null;
  status?: keyof typeof MatchJobStatus | null;
}

export type NewMatcherJobHistory = Omit<IMatcherJobHistory, 'id'> & { id: null };
