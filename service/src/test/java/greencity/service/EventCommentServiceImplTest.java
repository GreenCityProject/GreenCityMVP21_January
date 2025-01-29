package greencity.service;

import greencity.dto.event.EventCommentResponseDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
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
public class EventCommentServiceImplTest {
    @Mock
    private EventRepo eventRepo;

    @Mock
    private EventCommentRepo eventCommentRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl service;

    private Event event;
    private EventComment eventComment;
    private EventComment eventComment2;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setAuthor(new User());

        eventComment = new EventComment();
        eventComment.setId(1L);
        eventComment.setText("text1");
        eventComment.setCreatedDate(LocalDateTime.now());

        eventComment2 = new EventComment();
        eventComment2.setId(2L);
        eventComment2.setText("text2");
        eventComment2.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getCommentsByEventTest() {
        EventCommentResponseDto responseDto = new EventCommentResponseDto();
        responseDto.setId(1L);
        responseDto.setText("text1");
        responseDto.setCreatedDate(LocalDateTime.now());
        responseDto.setAuthor(new UserProfilePictureDto());

        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(eventCommentRepo.findByEvent(any(Event.class))).thenReturn(List.of(eventComment, eventComment2));
        when(modelMapper.map(eventComment, EventCommentResponseDto.class)).thenReturn(responseDto);

        List<EventCommentResponseDto> result = service.getCommentsByEvent(1L);

        verify(eventCommentRepo, times(1)).findByEvent(any(Event.class));
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getCommentsByEventNoEventTest() {
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> service.getCommentsByEvent(1L));

        verify(eventCommentRepo, times(0)).findByEvent(any(Event.class));
        Assertions.assertEquals("Event not found with id: 1", exception.getMessage());
    }

    @Test
    void countCommentsByEventTest() {
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(eventCommentRepo.countByEvent(any(Event.class))).thenReturn(66L);

        Long result = service.countCommentsByEvent(1L);

        verify(eventCommentRepo, times(1)).countByEvent(any(Event.class));
        Assertions.assertEquals(66L, result);
    }

    @Test
    void countCommentsByEventNoCommentsTest() {
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(eventCommentRepo.countByEvent(any(Event.class))).thenReturn(0L);

        Long result = service.countCommentsByEvent(1L);

        verify(eventCommentRepo, times(1)).countByEvent(any(Event.class));
        Assertions.assertEquals(0L, result);
    }

    @Test
    void countCommentsByEventNoEventTest() {
        when(eventRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> service.countCommentsByEvent(1L));

        verify(eventCommentRepo, times(0)).countByEvent(any(Event.class));
        Assertions.assertEquals("Event not found with id: 1", exception.getMessage());
    }
}
