package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Participant;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepo extends JpaRepository<Participant, Integer> {

    /**
     * Method to find all participants of a specific event
     *
     * @param event the {@link Event} instance
     * @return list of all participants
     */
    List<Participant> findByEvent(Event event);

    /**
     * Method to find all events a specific user is participating in.
     *
     * @param user the {@link User} instance.
     * @return list of {@link Participant} instances.
     */
    List<Participant> findByUser(User user);

    /**
     * Method to check if a user is a participant of a specific event.
     *
     * @param user  the {@link User} instance.
     * @param event the {@link Event} instance.
     * @return an {@link Optional} of {@link Participant}.
     */
    Optional<Participant> findByUserAndEvent(User user, Event event);

    /**
     * Method to get a Participant by its ID.
     *
     * @param id ID of the Participant.
     * @return an {@link Optional} of {@link Participant}.
     */
    Optional<Participant> findById(Long id);

    /**
     * Method to find all participants
     *
     * @return list of {@link Participant} instances
     * */
    List<Participant> findAll();

}
