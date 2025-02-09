package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.event.*;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventRequestDto eventRequestDto, @CurrentUser Principal currentUser, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        eventRequestDto.setAuthorEmail(currentUser.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequestDto));
    }

    @GetMapping("/myEvents")
    public ResponseEntity<EventProfilePreviewPageable> getEventsByUser(@CurrentUser Principal currentUser, @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllUserEvents(currentUser.getName(), pageable));
    }

    @GetMapping("/myEvents/past")
    public ResponseEntity<EventProfilePreviewPageable> getPastEventsByUser(@CurrentUser Principal currentUser, @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllUserPastEvents(currentUser.getName(), pageable));
    }

    @GetMapping("/myEvents/live")
    public ResponseEntity<EventProfilePreviewPageable> getLiveEventsByUser(@CurrentUser Principal currentUser, @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllUserLiveEvents(currentUser.getName(), pageable));
    }

    @GetMapping("/myEvents/upcoming")
    public ResponseEntity<EventProfilePreviewPageable> getUpcomingEventsByUser(@CurrentUser Principal currentUser, @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllUserUpcomingEvents(currentUser.getName(), pageable));
    }


}
