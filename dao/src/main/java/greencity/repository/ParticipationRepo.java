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

//        /**
//     * Method to find all participants of a specific event
//     *
//     * @param event the {@link Event} instance
//     * @return list of all participants
//     */
//    List<Participation> findByEvent(Event event); //Causes problems
//
//    /**
//     * Method to find all events a specific user is participating in.
//     *
//     * @param user the {@link User} instance.
//     * @return list of {@link Participation} instances.
//     */
//    List<Participation> findByUser(User user); //Causes problems
//
//    /**
//     * Method to check if a user is a participant of a specific event.
//     *
//     * @param user  the {@link User} instance.
//     * @param event the {@link Event} instance.
//     * @return an {@link Optional} of {@link Participation}.
//     */
//    Optional<Participation> findByUserAndEvent(User user, Event event); //Causes problems
//
//    /**
//     * Method to get a Participant by its ID.
//     *
//     * @param id ID of the Participant.
//     * @return an {@link Optional} of {@link Participation}.
//     */
//    Optional<Participation> findById(ParticipationKey id);

//    /**
//     * Method to find all participants
//     *
//     * @return list of {@link Participation} instances
//     * */
    //  List<Participation> findAll();

    @Query("SELECT p.id.user FROM Participation p WHERE p.id.event.id = :eventId")
    List<User> findUsersByEventId(@Param("eventId") Long eventId);

    @Query("SELECT p.id.event FROM Participation p WHERE p.id.user.id = :userId")
    List<Event> findEventsByUserId(@Param("userId") Long userId);
}
