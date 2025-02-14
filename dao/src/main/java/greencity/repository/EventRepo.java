package greencity.repository;

import greencity.entity.Event;
import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    /**
     * Method to get all events.
     *
     * @return list of all {@link Event} instances.
     */
    // List<Event> findAll();

    /**
     * Method to get an event by its ID.
     *
     * @param id ID of the event.
     * @return an {@link Optional} of {@link Event}.
     */
    Optional<Event> findById(Long id);

    /**
     * Method to get all events with a specific title (case-insensitive).
     *
     * @param title the title to search for.
     * @return list of {@link Event} instances with the specified title.
     */
    List<Event> findByTitleIgnoreCase(String title);

    /**
     * Method to find all events created by a specific author.
     *
     * @param author {@link User} who created the events.
     * @return list of {@link Event} instances.
     */
    List<Event> findAllByAuthor(User author);

    /**
     * Method to find all events that are open.
     *
     * @return list of open {@link Event} instances.
     */
    List<Event> findAllByIsOpenTrue();

    /**
     * Method to get all events sorted by creation date in descending order.
     *
     * @param pageable {@link Pageable} for pagination.
     * @return page of {@link Event} instances sorted by creation date.
     */
    Page<Event> findAllByOrderByCreationDateDesc(Pageable pageable);

    /**
     * Method to find events by title containing a specific keyword (case-insensitive).
     *
     * @param title keyword to search for.
     * @param pageable {@link Pageable} for pagination.
     * @return page of {@link Event} instances.
     */
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Method to get the count of all open events.
     *
     * @return count of open events.
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.isOpen = true")
    Long countAllOpenEvents();

    /**
     * Method to find all events within a specific date range.
     *
     * @param startDate start of the range.
     * @param endDate end of the range.
     * @return list of {@link Event} instances within the date range.
     */
    List<Event> findAllByCreationDateBetween(ZonedDateTime startDate, ZonedDateTime endDate);


}
