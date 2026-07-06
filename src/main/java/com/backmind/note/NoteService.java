package com.backmind.note;

import com.backmind.note.dto.CreateNoteRequest;
import com.backmind.note.dto.NoteResponse;
import com.backmind.note.dto.UpdateNoteRequest;
import com.backmind.note.entity.Note;
import com.backmind.note.repository.NoteRepository;
import com.backmind.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NoteResponse create(UUID userId, CreateNoteRequest request) {
        var now = Instant.now();
        var user = userRepository.findById(userId).orElseThrow();
        var note = new Note(user, request.content(), request.category(), now);
        return NoteResponse.from(noteRepository.saveAndFlush(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAll(UUID userId) {
        return noteRepository.findAllByUserId(userId).stream()
                .map(NoteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse findById(UUID userId, UUID noteId) {
        return noteRepository.findByIdAndUserId(noteId, userId)
                .map(NoteResponse::from)
                .orElseThrow(NoteNotFoundException::new);
    }

    @Transactional
    public NoteResponse update(UUID userId, UUID noteId, UpdateNoteRequest request) {
        var now = Instant.now();
        var note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(NoteNotFoundException::new);

        note.update(request.content(), request.category(), now);
        return NoteResponse.from(noteRepository.saveAndFlush(note));
    }

    @Transactional
    public void delete(UUID userId, UUID noteId) {
        var now = Instant.now();
        var note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(NoteNotFoundException::new);

        note.softDelete(now);
        noteRepository.saveAndFlush(note);
    }
}
