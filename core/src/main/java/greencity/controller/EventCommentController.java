package greencity.controller;

import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.service.EventCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @PostMapping
    public ResponseEntity<AddEventCommentDtoResponse> addComment(
            @PathVariable Long eventId,
            @RequestBody @Valid EventCommentRequestDto requestDto,
            @RequestParam Long userId) {
        return ResponseEntity.ok(eventCommentService.addComment(eventId, userId, requestDto));
    }
}
