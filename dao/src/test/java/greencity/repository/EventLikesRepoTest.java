package greencity.repository;

import greencity.entity.Event;
import greencity.entity.EventLikes;
import greencity.entity.EventLikesKey;
import greencity.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class EventLikesRepoTest {
    @Autowired
    private EventLikesRepo eventLikesRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    EventRepo eventRepo;

    private User user;
    private Event event;
    private EventLikes eventLikes;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        eventRepo.deleteAll();
        eventLikesRepo.deleteAll();

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
    }

    @Test
    void findAllTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        eventLikesRepo.save(eventLikes);

        List<EventLikes> result = eventLikesRepo.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_EventIdTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        eventLikesRepo.save(eventLikes);

        Optional<User> result = eventLikesRepo.findById_EventId(event.getId());

        if (result.isPresent()) {
            assertEquals("token", result.get().getRefreshTokenKey());
        }
    }

    @Test
    void findById_UserIdTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        eventLikesRepo.save(eventLikes);

        Optional<Event> result = eventLikesRepo.findById_UserId(user.getId());

        if (result.isPresent()) {
            assertEquals("Event description", result.get().getDescription());
        }
    }

    @Test
    void findById_EventIdNoSuchIdTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        eventLikesRepo.save(eventLikes);

        Optional<User> result = eventLikesRepo.findById_EventId(77L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_UserIdNoSuchIdTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        eventLikesRepo.save(eventLikes);

        Optional<Event> result = eventLikesRepo.findById_UserId(77L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllTest() {
        EventLikesKey eventLikesKey = new EventLikesKey(user, event);
        eventLikes = new EventLikes(eventLikesKey, true, false);

        List<EventLikes> result;

        eventLikesRepo.save(eventLikes);

        result = eventLikesRepo.findAll();
        assertEquals(1, result.size());

        eventLikesRepo.deleteAll();
        result = eventLikesRepo.findAll();
        assertEquals(0, result.size());
    }
}
