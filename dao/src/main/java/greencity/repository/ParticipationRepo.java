package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Participation;
import greencity.entity.ParticipationKey;
import greencity.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepo extends JpaRepository<Participation, ParticipationKey> {

    @Query("SELECT p.id.user FROM Participation p WHERE p.id.event.id = :eventId")
    List<User> findUsersByEventId(@Param("eventId") Long eventId);

    @Query("SELECT p.id.event FROM Participation p WHERE p.id.user.id = :userId")
    List<Event> findEventsByUserId(@Param("userId") Long userId);

}
