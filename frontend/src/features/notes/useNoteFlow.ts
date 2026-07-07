import { useState } from 'react';

import { FeedNote, ReviewAction } from '../feed/HomeFeedScreen';
import { CreateNoteInput } from './CreateNoteScreen';
import { MyNote } from './MyNotesScreen';
import { noteApi } from './noteApi';

export function useNoteFlow(token: string) {
  const [notes, setNotes] = useState<FeedNote[]>([]);
  const [allNotes, setAllNotes] = useState<MyNote[]>([]);
  const [feedLoading, setFeedLoading] = useState(false);
  const [listLoading, setListLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  async function loadFeed() {
    await run(setFeedLoading, async () => {
      const response = await noteApi.today(token);
      setNotes(
        response.map((note) => ({
          id: note.id,
          content: note.content,
          category: note.category ?? 'BackMind note',
          createdAt: note.createdAt,
        })),
      );
    });
  }

  async function create(note: CreateNoteInput) {
    await run(setSaving, async () => {
      await noteApi.create(token, note);
      setSuccess('Note saved');
    });
  }

  async function loadAll() {
    await run(setListLoading, async () => {
      const response = await noteApi.findAll(token);
      setAllNotes(
        response.map(({ id, content, category, createdAt }) => ({
          id,
          content,
          category,
          createdAt,
        })),
      );
    });
  }

  async function review(noteId: string, action: ReviewAction) {
    await run(setFeedLoading, async () => {
      await noteApi.review(token, noteId, action);
      setNotes((current) => current.filter((note) => note.id !== noteId));
    });
  }

  async function run(
    setLoading: (loading: boolean) => void,
    request: () => Promise<void>,
  ) {
    if (!token) return;
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      await request();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  }

  return {
    allNotes,
    create,
    error,
    feedLoading,
    listLoading,
    loadAll,
    loadFeed,
    notes,
    review,
    saving,
    success,
  };
}
