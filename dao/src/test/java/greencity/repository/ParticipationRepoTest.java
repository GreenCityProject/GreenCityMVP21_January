package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Participation;
import greencity.entity.ParticipationKey;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "spring.liquibase.enabled=false"
})
public class ParticipationRepoTest {
    @Autowired
    private ParticipationRepo participationRepo;
    private User author;
    private Event event;

    @BeforeEach
    void setUp() {
        participationRepo.deleteAll();
        author = new User();
        author.setFirstName("John");
        author.setEmail("john.doe@mail.com");
        author.setDateOfRegistration(LocalDateTime.now());
        author.setName("John Doe");
        author.setRefreshTokenKey("token");
        author.setRole(Role.ROLE_USER);
        author.setId(1L);

        event = new Event();
        event.setAuthor(author);
        event.setTitle("Sample Event");
        event.setDescription("Event description");
        event.setCreationDate(ZonedDateTime.now());
        event.setOpen(true);
        event.setDuration(60);
        event.setId(1L);
    }

    @Test
    void findByEventTest() {
        Participation participation = new Participation();
        participation.setId(new ParticipationKey(event.getId(), author.getId()));

//        participation.setEvent(event);
//        participation.setUser(author);
        participationRepo.save(participation);

        List<Participation> result = participationRepo.findAll();
        assertEquals(1, result.size());

    }

}
