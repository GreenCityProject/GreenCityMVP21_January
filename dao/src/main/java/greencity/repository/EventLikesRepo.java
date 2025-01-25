package greencity.repository;

import greencity.entity.EventLikes;
import greencity.entity.EventLikesKey;
import greencity.entity.User;
import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventLikesRepo extends JpaRepository<EventLikes, EventLikesKey> {
    /**
     * Method to find all likes for a specific event.
     *
     * @param event the {@link Event} instance.
     * @return list of {@link EventLikes} instances.
     */
    List<EventLikes> findByEvent(Event event);

    /**
     * Method to find all events liked by a specific user.
     *
     * @param user the {@link User} instance.
     * @return list of {@link EventLikes} instances.
     */
    List<EventLikes> findByUser(User user);

    /**
     * Method to check if a specific user liked a specific event.
     *
     * @param user  the {@link User} instance.
     * @param event the {@link Event} instance.
     * @return an {@link Optional} of {@link EventLikes}.
     */
    Optional<EventLikes> findByUserAndEvent(User user, Event event);

    /**
     * Method to get a EventLike by its ID.
     *
     * @param id ID of the Participant.
     * @return an {@link Optional} of {@link EventLikes}.
     */
    Optional<EventLikes> findById(EventLikesKey id);

    /**
     * Method to count the total number of likes for a specific event.
     *
     * @param event the {@link Event} instance.
     * @return the number of likes for the event.
     */
    @Query("SELECT COUNT(el) FROM EventLikes el WHERE el.event = :event")
    long countByEvent(Event event); // Not sure about it, but added for future
}