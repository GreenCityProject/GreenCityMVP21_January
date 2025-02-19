package greencity.repository;

import greencity.entity.EventLikes;
import greencity.entity.EventLikesKey;
import greencity.entity.User;
import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventLikesRepo extends JpaRepository<EventLikes, EventLikesKey> {

    @Query("SELECT e.id.user FROM EventLikes e WHERE e.id.event.id = :eventId")
    List<User> findUsersByEventId(@Param("eventId") Long eventId);

    @Query("SELECT e.id.event FROM EventLikes e WHERE e.id.user.id = :userId")
    List<Event> findEventsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM EventLikes e WHERE e.id.event.id = :eventId")
    int countLikesByEventId(@Param("eventId") Long eventId);
}