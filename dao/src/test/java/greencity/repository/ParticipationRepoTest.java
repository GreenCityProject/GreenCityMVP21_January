package greencity.repository;

import greencity.entity.*;
import greencity.enums.Role;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
public class ParticipationRepoTest {
    @Autowired
    private ParticipationRepo participationRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private UserRepo userRepo;

    private User author;
    private Event event;

    @BeforeEach
    void setUp() {
        participationRepo.deleteAll();
        userRepo.deleteAll();
        eventRepo.deleteAll();

        author = new User();
        author.setFirstName("John");
        author.setEmail("john.doe@mail.com");
        author.setDateOfRegistration(LocalDateTime.now());
        author.setName("John Doe");
        author.setRefreshTokenKey("token");
        author.setRole(Role.ROLE_USER);
        userRepo.save(author);

        event = new Event();
        event.setAuthor(author);
        event.setTitle("Sample Event");
        event.setDescription("Event description");
        event.setCreationDate(ZonedDateTime.now());
        event.setOpen(true);
        event.setDuration(60);
        eventRepo.save(event);
    }

    @Test
    void findAllTest() {
        ParticipationKey participationKey = new ParticipationKey(author, event);

        Participation participation = new Participation();
        participation.setId(participationKey);

        participationRepo.save(participation);

        List<Participation> result = participationRepo.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_EventIdTest() {
        ParticipationKey participationKey = new ParticipationKey(author, event);

        Participation participation = new Participation();
        participation.setId(participationKey);

        participationRepo.save(participation);

        Optional<User> result = participationRepo.findById_EventId(2L);

        if (result.isPresent()) {
            assertEquals("John", result.get().getFirstName());
        }
    }
}
