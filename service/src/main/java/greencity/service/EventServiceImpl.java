package greencity.service;

import greencity.dto.event.*;
import greencity.entity.*;
import greencity.repository.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final InitiativeTypeRepo initiativeTypeRepo;
    private final ImageRepo imageRepo;
    private final UserRepo userRepo;
    private final EventDateInfoRepo eventDateInfoRepo;
    private final EmailService emailService;

    private void validateEventRequest(EventRequestDto eventRequestDto) {
        if (eventRequestDto.getEventDays() == null || eventRequestDto.getEventDays().isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one event day.");
        }
        for (EventDateInfoRequestDto e : eventRequestDto.getEventDays()) {
            if (e.getIsOnline() && e.getUrl() == null) {
                throw new IllegalArgumentException("If event is online it has to have the url");
            }
            if (e.getIsPlace() && e.getLocation() == null) {
                throw new IllegalArgumentException("If event is offline it has to have the location");
            }
        }
    }

    private Image handleImages(EventRequestDto eventRequestDto) {
        Set<Image> images = new HashSet<>();
        Image mainImage = null;

        if (eventRequestDto.getImages() == null || eventRequestDto.getImages().isEmpty()) {
            Image defaultImage = imageRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("Default image not found"));
            images.add(defaultImage);
            mainImage = defaultImage;
        } else {
            for (ImageRequestDto imageRequestDto : eventRequestDto.getImages()) {
                Image image = saveImage(imageRequestDto.getImagePath());
                images.add(image);
            }
            if (eventRequestDto.getMainImage() != null) {
                mainImage = saveImage(eventRequestDto.getMainImage().getImagePath());
            }

            if (mainImage == null) {
                if (eventRequestDto.getImages() != null && !eventRequestDto.getImages().isEmpty()) {
                    mainImage = imageRepo.findByImagePath(eventRequestDto.getImages().get(0).getImagePath()).orElse(null);
                } else {
                    mainImage = imageRepo.findById(1L).orElse(null);
                }
            }
        }
        return mainImage;
    }

    private Event createEventFromRequest(EventRequestDto eventRequestDto, User author, Image mainImage, Set<Image> images) {
        Event event = modelMapper.map(eventRequestDto, Event.class);
        event.setCreationDate(ZonedDateTime.now());
        event.setAuthor(author);
        event.setImages(images);
        event.setMainImage(mainImage);
        return event;
    }

    private void saveEventDateInfo(EventRequestDto eventRequestDto, Event savedEvent) {
        for (EventDateInfoRequestDto infoRequestDto : eventRequestDto.getEventDays()) {
            EventDateInfo eventDateInfo = modelMapper.map(infoRequestDto, EventDateInfo.class);
            eventDateInfo.setEvent(savedEvent);
            eventDateInfoRepo.save(eventDateInfo);
        }
    }

    private void sendEventCreationEmail(User author, Event event) {
        String emailBody = String.format(
                "Dear %s,<br><br>Your event \"%s\" has been created.<br><br>Best regards,<br>Green City team",
                author.getName(), event.getTitle());
        String emailSubject = "\uD83D\uDD14 Your Event Creation Status";

        try {
            emailService.sendEmail(author.getEmail(), emailSubject, emailBody);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send event creation email", e);
        }
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        validateEventRequest(eventRequestDto);

        Event event = modelMapper.map(eventRequestDto, Event.class);
        User author = userRepo.findByEmail(eventRequestDto.getAuthorEmail()).orElse(null);

        Image mainImage = handleImages(eventRequestDto);
        Set<Image> images = (eventRequestDto.getImages() == null || eventRequestDto.getImages().isEmpty())
                ? Set.of(Objects.requireNonNull(imageRepo.findById(1L).orElse(null))) : eventRequestDto.getImages().stream()
                .map(i -> modelMapper.map(i, Image.class))
                .map(image -> saveImage(image.getImagePath()))
                .collect(Collectors.toSet());

        event.setImages(images);

        Event savedEvent = createEventFromRequest(eventRequestDto, author, mainImage, images);
        Event savedEventInRepo = eventRepo.save(savedEvent);

        saveEventDateInfo(eventRequestDto, savedEventInRepo);

        List<InitiativeType> initiativeTypes = eventRequestDto.getInitiativeTypes().stream()
                .map(i -> initiativeTypeRepo.findByName(i.getName())
                        .orElseThrow(() -> new EntityNotFoundException("Initiative type not found: " + i.getName())))
                .collect(Collectors.toList());
        savedEventInRepo.setInitiativeTypes(initiativeTypes);

        EventResponseDto eventResponseDto = modelMapper.map(savedEventInRepo, EventResponseDto.class);

        List<EventDateInfoResponseDto> eventDateInfoResponseDtos = eventDateInfoRepo.findByEvent(savedEventInRepo).stream()
                .map(e -> modelMapper.map(e, EventDateInfoResponseDto.class))
                .collect(Collectors.toList());

        eventResponseDto.setEventDays(eventDateInfoResponseDtos);

        assert author != null;
        sendEventCreationEmail(author, savedEventInRepo);

        return eventResponseDto;
    }

    private Image saveImage(String imagePath) {
        Optional<Image> existingImage = imageRepo.findByImagePath(imagePath);

        if (existingImage.isPresent()) {
            return existingImage.get();
        } else {
            Image newImage = Image.builder().imagePath(imagePath).build();
            return imageRepo.save(newImage);
        }
    }

    @Override
    public EventResponseDto updateEvent(Long id, EventRequestDto eventRequestDto) {
        return null;
    }

    @Override
    public void deleteEvent(Long id) {

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventResponseDto> getEventById(Long id) {
        return eventRepo.findById(id)
                .map(event -> modelMapper.map(event, EventResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDto> findEventsByTitle(String title) {
        return List.of();
    }

    @Override
    public List<EventResponseDto> getAllOpenEvents() {
        return List.of();
    }
}
