package com.backmind.resurfacing;

import com.backmind.note.dto.NoteResponse;
import com.backmind.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final ResurfacingService resurfacingService;

    public FeedController(ResurfacingService resurfacingService) {
        this.resurfacingService = resurfacingService;
    }

    @GetMapping("/today")
    public List<NoteResponse> today(@AuthenticationPrincipal User user) {
        return resurfacingService.today(user);
    }

    @GetMapping("/lost")
    public List<NoteResponse> lost(@AuthenticationPrincipal User user) {
        return resurfacingService.lost(user);
    }
}
