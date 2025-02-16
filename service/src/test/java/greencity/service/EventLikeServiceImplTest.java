package greencity.service;

import greencity.entity.Event;
import greencity.entity.User;
import greencity.repository.EventLikesRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventLikeServiceImplTest {
    @Mock
    private EventLikesRepo eventLikesRepo;

    @Mock
    private EventRepo eventRepo;

    @InjectMocks
    private EventLikeServiceImpl eventLikeService;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event();
        event.setId(1L);
        event.setAuthor(new User());
    }

    @Test
    void countLikesTest() {
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventLikesRepo.countLikesByEventId(event.getId())).thenReturn(1);

        long result = eventLikeService.countLikes(1L);

        verify(eventLikesRepo, times(1)).countLikesByEventId(event.getId());
        Assertions.assertEquals(1L, result);
    }

    @Test
    void countLikesNoSuchEventTest() {
        when(eventRepo.findById(event.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> eventLikeService.countLikes(1L));

        verify(eventLikesRepo, times(0)).countLikesByEventId(event.getId());
        Assertions.assertEquals("Event not found with id: 1", exception.getMessage());
    }
}
