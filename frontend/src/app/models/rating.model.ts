export interface Rating {
  id?: number;
  boardGameId: number;
  userId?: string;
  score: number;        // 1 à 5
  comment?: string;
  createdAt?: string;
}