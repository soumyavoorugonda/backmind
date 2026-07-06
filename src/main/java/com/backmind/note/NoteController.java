package com.backmind.note;

import com.backmind.note.dto.CreateNoteRequest;
import com.backmind.note.dto.NoteResponse;
import com.backmind.note.dto.UpdateNoteRequest;
import com.backmind.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.create(user.id(), request);
    }

    @GetMapping
    public List<NoteResponse> findAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return noteService.findAll(user.id());
    }

    @GetMapping("/{id}")
    public NoteResponse findById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("id") UUID id
    ) {
        return noteService.findById(user.id(), id);
    }

    @PutMapping("/{id}")
    public NoteResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateNoteRequest request
    ) {
        return noteService.update(user.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("id") UUID id
    ) {
        noteService.delete(user.id(), id);
    }
}
