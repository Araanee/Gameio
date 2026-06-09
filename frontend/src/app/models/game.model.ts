export interface BoardGame {
  id?: number;
  title: string;
  description?: string;
  imageUrl?: string;
  genre?: string;
  minPlayers?: number;
  maxPlayers?: number;
  year?: number;
  averageRating?: number;
  status?: 'PENDING' | 'APPROVED' | 'REJECTED';
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
