package greencity.service;

import greencity.dto.event.EventResponseDto;
import greencity.dto.event.ParticipationRequestDto;
import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventDateInfoRepo;
import greencity.repository.EventRepo;
import greencity.repository.ParticipationRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipationServiceImplTest {
    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ParticipationRepo participationRepo;

    @Mock
    private EventDateInfoRepo eventDateInfoRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ParticipationServiceImpl participationService;

    private Event event;
    private Event event2;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Masha");
        user.setEmail("masha@gmail.com");
        user.setRole(Role.ROLE_USER);
        user.setDateOfRegistration(LocalDateTime.now());
        user.setRefreshTokenKey("key");

        user2 = new User();
        user2.setId(2L);
        user.setName("Vasyl");
        user.setEmail("vasyl@gmail.com");
        user.setRole(Role.ROLE_USER);
        user.setDateOfRegistration(LocalDateTime.now());
        user.setRefreshTokenKey("key2");

        event = new Event();
        event.setId(1L);
        event.setAuthor(new User());

        event2 = new Event();
        event2.setId(2L);
        event2.setAuthor(new User());
    }

    @Test
    void addParticipationTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(participationRepo.save(any(Participation.class))).thenReturn(new Participation());

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setUserId(1L);
        participationRequestDto.setEventId(1L);

        participationService.addParticipation(participationRequestDto);

        verify(participationRepo, times(1)).save(argThat(participation ->
                participation.getId().getUser().equals(user) &&
                        participation.getId().getEvent().equals(event)));
    }

    @Test
    void addParticipationNoUserTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setUserId(22L);
        participationRequestDto.setEventId(1L);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> participationService.addParticipation(participationRequestDto));
        Assertions.assertEquals(exception.getMessage(), "User not found with id: 22");
        verify(participationRepo, times(0)).save(any(Participation.class));
    }

    @Test
    void removeParticipationTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo().setEventTimeStart(LocalDateTime.of(2025, 3, 3, 12, 30))));
        when(participationRepo.findById(any(ParticipationKey.class))).thenReturn(Optional.of(new Participation(new ParticipationKey(user, event))));
        doNothing().when(participationRepo).delete(any(Participation.class));

        participationService.removeParticipation(1L, 1L);

        verify(participationRepo, times(1)).delete(argThat(participation ->
                participation.getId().getUser().equals(user) &&
                        participation.getId().getEvent().equals(event)));
    }

    @Test
    void removeParticipationNoParticipationTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(participationRepo.findById(any(ParticipationKey.class))).thenReturn(Optional.empty());
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo().setEventTimeStart(LocalDateTime.of(2025, 3, 3, 12, 30))));

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> participationService.removeParticipation(1L, 1L));
        verify(participationRepo, times(0)).delete(any(Participation.class));
        Assertions.assertEquals("Participation not found", exception.getMessage());
    }

    @Test
    void removeParticipationInThePastTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(eventDateInfoRepo.findByEvent(any(Event.class))).thenReturn(List.of(new EventDateInfo().setEventTimeStart(LocalDateTime.now())));

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> participationService.removeParticipation(1L, 1L));
        verify(participationRepo, times(0)).delete(any(Participation.class));
        Assertions.assertEquals("You cannot remove the participation from the event that is in the past", exception.getMessage());
    }

    @Test
    void getUsersByEventIdTest() {
        UserProfilePictureDto userProfilePictureDto = new UserProfilePictureDto();
        userProfilePictureDto.setId(1L);
        userProfilePictureDto.setName("Name");
        userProfilePictureDto.setProfilePicturePath("path");

        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(participationRepo.findUsersByEventId(any(Long.class))).thenReturn(List.of(user, user2));
        when(modelMapper.map(any(User.class), eq(UserProfilePictureDto.class))).thenReturn(userProfilePictureDto);

        List<UserProfilePictureDto> result = participationService.getUsersByEventId(1L);

        verify(participationRepo, times(1)).findUsersByEventId(any(Long.class));
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getUsersByEventIdNoUsersTest() {
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(participationRepo.findUsersByEventId(any(Long.class))).thenReturn(List.of());

        List<UserProfilePictureDto> result = participationService.getUsersByEventId(1L);

        verify(participationRepo, times(1)).findUsersByEventId(any(Long.class));
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void getEventsByUserIdTest() {
        EventResponseDto eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(1L);
        eventResponseDto.setAuthor(new AuthorDto());

        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(participationRepo.findEventsByUserId(any(Long.class))).thenReturn(List.of(event));
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(eventResponseDto);

        List<EventResponseDto> result = participationService.getEventsByUserId(1L);

        verify(participationRepo, times(1)).findEventsByUserId(any(Long.class));
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getEventsByUserIdNoEventsTest() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(participationRepo.findEventsByUserId(any(Long.class))).thenReturn(List.of());

        List<EventResponseDto> result = participationService.getEventsByUserId(1L);

        verify(participationRepo, times(1)).findEventsByUserId(any(Long.class));
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void isUserParticipatingTest() {
        when(userRepo.existsById(any(Long.class))).thenReturn(true);
        when(eventRepo.existsById(any(Long.class))).thenReturn(true);
        when(participationRepo.findEventsByUserId(any(Long.class))).thenReturn(List.of(event, event2));

        boolean result = participationService.isUserParticipating(1L, 1L);

        verify(participationRepo, times(1)).findEventsByUserId(any(Long.class));
        Assertions.assertTrue(result);
    }

    @Test
    void isUserParticipatingEventNotFoundTest() {
        when(userRepo.existsById(any(Long.class))).thenReturn(true);
        when(eventRepo.existsById(any(Long.class))).thenReturn(false);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> participationService.isUserParticipating(1L, 1L));

        verify(participationRepo, times(0)).findEventsByUserId(any(Long.class));
        Assertions.assertEquals("Event not found with id: 1", exception.getMessage());
    }
}
