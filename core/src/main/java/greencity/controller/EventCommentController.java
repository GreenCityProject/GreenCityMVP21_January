package greencity.controller;

import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentRequestDto;
import greencity.dto.event.EventCommentResponseDto;
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

    @GetMapping("/count")
    public ResponseEntity<Long> countComments(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.countCommentsByEvent(eventId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<EventCommentResponseDto> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(eventCommentService.getCommentById(commentId));
    }
}
