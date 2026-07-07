import { apiClient } from '../../api/client';
import { ReviewAction } from '../feed/HomeFeedScreen';
import { CreateNoteInput } from './CreateNoteScreen';

export type NoteResponse = {
  id: string;
  content: string;
  category: string | null;
  createdAt: string;
  updatedAt: string;
  lastSeenAt: string | null;
  nextReviewAt: string | null;
  currentIntervalDays: number;
  status: 'ACTIVE' | 'ARCHIVED' | 'DELETED';
  usefulnessScore: number;
  beliefStatus: 'UNKNOWN' | 'STILL_BELIEVE' | 'NO_LONGER_BELIEVE' | 'UNSURE';
};

export const noteApi = {
  create(token: string, note: CreateNoteInput) {
    return apiClient<NoteResponse>('/api/notes', {
      body: { ...note, category: note.category || null },
      method: 'POST',
      token,
    });
  },

  today(token: string) {
    return apiClient<NoteResponse[]>('/api/feed/today', { token });
  },

  findAll(token: string) {
    return apiClient<NoteResponse[]>('/api/notes', { token });
  },

  review(token: string, noteId: string, feedbackType: ReviewAction) {
    return apiClient<NoteResponse>(`/api/notes/${noteId}/review`, {
      body: { feedbackType, userResponse: null },
      method: 'POST',
      token,
    });
  },
};
