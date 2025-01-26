package greencity.repository;

import greencity.entity.*;
import greencity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "spring.liquibase.enabled=false"
})
public class EventCommentLikesRepoTest {
    @Autowired
    EventCommentLikesRepo eventCommentLikesRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    EventCommentRepo eventCommentRepo;
    @Autowired
    EventRepo eventRepo;

    private User user;
    private Event event;
    private EventComment eventComment;
    private EventCommentLikes eventCommentLikes;

    @BeforeEach
    void setUp() {
        eventCommentLikesRepo.deleteAll();
        userRepo.deleteAll();
        eventRepo.deleteAll();
        eventCommentRepo.deleteAll();

        user = new User();
        user.setFirstName("John");
        user.setEmail("john.doe@mail.com");
        user.setDateOfRegistration(LocalDateTime.now());
        user.setName("John Doe");
        user.setRefreshTokenKey("token");
        user.setRole(Role.ROLE_USER);
        userRepo.save(user);

        event = new Event();
        event.setAuthor(user);
        event.setTitle("Sample Event");
        event.setDescription("Event description");
        event.setCreationDate(ZonedDateTime.now());
        event.setOpen(true);
        event.setDuration(60);
        eventRepo.save(event);

        eventComment = new EventComment();
        eventComment.setText("Event comment");
        eventComment.setUser(user);
        eventComment.setCreatedDate(LocalDateTime.now());
        eventCommentRepo.save(eventComment);
    }

    @Test
    void findAllTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        eventCommentLikesRepo.save(eventCommentLikes);

        List<EventCommentLikes> result = eventCommentLikesRepo.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_EventCommentIdTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        eventCommentLikesRepo.save(eventCommentLikes);

        Optional<User> result = eventCommentLikesRepo.findById_EventCommentId(eventComment.getId());
        if (result.isPresent()) {
            assertEquals("john.doe@mail.com", result.get().getEmail());
        }
    }

    @Test
    void findById_UserIdTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        eventCommentLikesRepo.save(eventCommentLikes);

        Optional<EventComment> result = eventCommentLikesRepo.findById_UserId(user.getId());
        if (result.isPresent()) {
            assertEquals("Event comment", result.get().getText());
        }
    }

    @Test
    void findById_UserIdNoSuchIdTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        eventCommentLikesRepo.save(eventCommentLikes);

        Optional<EventComment> result = eventCommentLikesRepo.findById_UserId(77L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_EventCommentIdNoSuchIdTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        eventCommentLikesRepo.save(eventCommentLikes);

        Optional<User> result = eventCommentLikesRepo.findById_EventCommentId(77L);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllTest() {
        EventCommentLikesKey eventCommentLikesKey = new EventCommentLikesKey(user, eventComment);
        eventCommentLikes = new EventCommentLikes(eventCommentLikesKey, true, false);

        List<EventCommentLikes> result;

        eventCommentLikesRepo.save(eventCommentLikes);

        result = eventCommentLikesRepo.findAll();
        assertEquals(1, result.size());

        eventCommentLikesRepo.deleteAll();
        result = eventCommentLikesRepo.findAll();
        assertEquals(0, result.size());
    }
}
