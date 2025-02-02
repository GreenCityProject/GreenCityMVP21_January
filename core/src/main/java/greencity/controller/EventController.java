package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.event.*;
import greencity.service.EventService;
import greencity.service.ImageStorageClient;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody EventRequestDto eventRequestDto, @CurrentUser Principal currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventRequestDto));

    }

//    @PutMapping
//    public ResponseEntity<?> update(@Validated @RequestBody EventRequestDto eventRequestDto) {}
}
