package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Participation;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepo extends JpaRepository<Participation, Integer> {

    /**
     * Method to find all participants of a specific event
     *
     * @param event the {@link Event} instance
     * @return list of all participants
     */
    List<Participation> findByEvent(Event event);

    /**
     * Method to find all events a specific user is participating in.
     *
     * @param user the {@link User} instance.
     * @return list of {@link Participation} instances.
     */
    List<Participation> findByUser(User user);

    /**
     * Method to check if a user is a participant of a specific event.
     *
     * @param user  the {@link User} instance.
     * @param event the {@link Event} instance.
     * @return an {@link Optional} of {@link Participation}.
     */
    Optional<Participation> findByUserAndEvent(User user, Event event);

    /**
     * Method to get a Participant by its ID.
     *
     * @param id ID of the Participant.
     * @return an {@link Optional} of {@link Participation}.
     */
    Optional<Participation> findById(Long id);

    /**
     * Method to find all participants
     *
     * @return list of {@link Participation} instances
     * */
    List<Participation> findAll();

}
