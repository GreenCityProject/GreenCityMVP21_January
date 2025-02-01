package greencity.controller;

import greencity.dto.event.*;
import greencity.service.EventService;
import greencity.service.ImageStorageClient;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    private final ImageStorageClient imageStorageClient;

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody EventCreateDTO eventCreateDTO, @Parameter(hidden = true) Principal principal) {
        List<MultipartFile> files = eventCreateDTO.getFiles();
        EventRequestDto event = eventCreateDTO.getEvent();
        ImageRequestDto mainImage = eventCreateDTO.getMainImage();
        String containerName = eventCreateDTO.getContainerName();
        ImageRequestDto chosenOfProposedImage = eventCreateDTO.getChosenOfProposedImage();

        EventGenericDto eventGenericDto = new EventGenericDto();

        List<String> images;
        try {
            images = imageStorageClient.uploadImage(containerName, files, chosenOfProposedImage);
            eventGenericDto.setImages(images);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        if (mainImage == null) {
            event.setMainImage(ImageRequestDto.builder().imagePath(images.getFirst()).build());
        }

        eventGenericDto.setEvent(eventService.createEvent(event));

        return ResponseEntity.status(HttpStatus.CREATED).body(eventGenericDto);
    }
}
