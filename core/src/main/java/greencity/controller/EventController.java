package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.event.*;
import greencity.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> create(@Valid @RequestBody EventRequestDto eventRequestDto, @CurrentUser Principal currentUser) {

        eventRequestDto.setAuthorEmail(currentUser.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequestDto));
    }

//    @PutMapping
//    public ResponseEntity<?> update(@Validated @RequestBody EventRequestDto eventRequestDto) {}
}
