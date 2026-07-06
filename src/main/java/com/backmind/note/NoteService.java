package com.backmind.note;

import com.backmind.note.dto.CreateNoteRequest;
import com.backmind.note.dto.NoteResponse;
import com.backmind.note.dto.UpdateNoteRequest;
import com.backmind.note.entity.Note;
import com.backmind.note.repository.NoteRepository;
import com.backmind.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public NoteResponse create(User user, CreateNoteRequest request) {
        var note = new Note(user, request.content(), request.category());
        return NoteResponse.from(noteRepository.saveAndFlush(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAll(User user) {
        return noteRepository.findAllByUserId(user.getId()).stream()
                .map(NoteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse findById(User user, UUID noteId) {
        return noteRepository.findByIdAndUserId(noteId, user.getId())
                .map(NoteResponse::from)
                .orElseThrow(NoteNotFoundException::new);
    }

    @Transactional
    public NoteResponse update(User user, UUID noteId, UpdateNoteRequest request) {
        var note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(NoteNotFoundException::new);

        note.update(request.content(), request.category());
        return NoteResponse.from(noteRepository.saveAndFlush(note));
    }

    @Transactional
    public void delete(User user, UUID noteId) {
        var note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(NoteNotFoundException::new);

        note.softDelete();
        noteRepository.saveAndFlush(note);
    }
}
