package greencity.repository;

import greencity.entity.EventDislike;
import greencity.entity.EventLike;
import greencity.entity.User;
import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventDislikesRepo extends JpaRepository<EventDislike, Long> {
    /**
     * Method to find all dislikes for a specific event.
     *
     * @param event the {@link Event} instance.
     * @return list of {@link EventDislike} instances.
     */
    List<EventDislike> findByEvent(Event event);

    /**
     * Method to find all events disliked by a specific user.
     *
     * @param user the {@link User} instance.
     * @return list of {@link EventDislike} instances.
     */
    List<EventDislike> findByUser(User user);

    /**
     * Method to check if a specific user disliked a specific event.
     *
     * @param user  the {@link User} instance.
     * @param event the {@link Event} instance.
     * @return an {@link Optional} of {@link EventDislike}.
     */
    Optional<EventDislike> findByUserAndEvent(User user, Event event);

    /**
     * Method to get a EventDislike by its ID.
     *
     * @param id ID of the Participant.
     * @return an {@link Optional} of {@link EventDislike}.
     */
    Optional<EventDislike> findById(Long id);

    /**
     * Method to count the total number of dislikes for a specific event.
     *
     * @param event the {@link Event} instance.
     * @return the number of dislikes for the event.
     */
    @Query("SELECT COUNT(ed) FROM EventDislike ed WHERE ed.event = :event")
    long countByEvent(Event event); // Not sure about it, but added for future
}
