package greencity.service;

import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.repository.EventCommentLikesRepo;
import greencity.repository.EventCommentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommentLikesServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;
    @Mock
    private EventCommentLikesRepo eventCommentLikesRepo;

    @InjectMocks
    private EventCommentLikesServiceImpl eventCommentLikesService;

    private EventComment eventComment;

    @BeforeEach
    void setUp() {
        eventComment = new EventComment();
        eventComment.setId(1L);
        eventComment.setUser(new User());
        eventComment.setText("test");
        eventComment.setEvent(new Event());
        eventComment.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void countLikesByEventCommentIdTest() {
        when(eventCommentRepo.findById(anyLong())).thenReturn(Optional.of(eventComment));
        when(eventCommentLikesRepo.countLikesByEventCommentId(anyLong())).thenReturn(2L);

        long likes = eventCommentLikesService.countLikesByEventCommentId(1L);

        verify(eventCommentLikesRepo, times(1)).countLikesByEventCommentId(anyLong());
        Assertions.assertEquals(2L, likes);
    }

    @Test
    void countLikesByEventCommentIdNoEventCommentTest() {
        when(eventCommentRepo.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> eventCommentLikesService.countLikesByEventCommentId(1L));

        verify(eventCommentLikesRepo, times(0)).countLikesByEventCommentId(anyLong());
        Assertions.assertEquals("EventComment not found with id: 1", exception.getMessage());
    }
}
